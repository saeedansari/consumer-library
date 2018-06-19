package com.loyalty.consumer.configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.aws.inbound.kinesis.KinesisMessageDrivenChannelAdapter;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;

@Configuration
@EnableIntegration
@IntegrationComponentScan("com.loyalty.consumer")
public class KinesisConsumerConfiguration {

  @Value("${cloud.aws.region.static}")
  private String awsRegion;

  @Autowired
  StreamChannelConfigurations streamChannelConfigurations;

  @Bean
  public AmazonKinesis amazonKinesis() {
    AmazonKinesis amazonKinesis;

    System.setProperty(SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true");
    String url = streamChannelConfigurations.getUrl();

    amazonKinesis = AmazonKinesisAsyncClientBuilder.standard()
        .withClientConfiguration(
            new ClientConfiguration()
                .withMaxErrorRetry(streamChannelConfigurations.getRetries())
                .withConnectionTimeout(streamChannelConfigurations.getConnectionTimeout()))
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(
                url,
                Regions.fromName(awsRegion).getName()))
        .build();

    return amazonKinesis;

  }

  public KinesisMessageDrivenChannelAdapter kinesisInboundChannelChannel(
      AmazonKinesis amazonKinesis, String[] streamNames) {
    KinesisMessageDrivenChannelAdapter adapter =
        new KinesisMessageDrivenChannelAdapter(amazonKinesis, streamNames);
    adapter.setConverter(null);
    adapter.setOutputChannel(kinesisReceiveChannel());
    adapter.setCheckpointStore();
    return adapter;
  }

  public PollableChannel kinesisReceiveChannel() {
    QueueChannel queueChannel = new QueueChannel();
    return queueChannel;
  }

  @Bean
  public IntegrationFlow kinesisIntegration() {
    final Set<String> streamNames = streamChannelConfigurations.getStreams().keySet();
    return IntegrationFlows.from(kinesisInboundChannelChannel(amazonKinesis(),
        streamNames.toArray(new String[streamNames.size()])))
        .route(router())
        .get();
  }


  public HeaderValueRouter router() {
    HeaderValueRouter router = new HeaderValueRouter(MessageHeaders.STREAM_NAME);
    streamChannelConfigurations.getStreams().forEach((k, v) ->
        router.setChannelMapping(k, v)
    );
    return router;
  }

}
