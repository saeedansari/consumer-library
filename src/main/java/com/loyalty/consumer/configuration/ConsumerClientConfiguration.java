package com.loyalty.consumer.configuration;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBLockClientOptions.AmazonDynamoDBLockClientOptionsBuilder;
import com.amazonaws.services.dynamodbv2.CreateDynamoDBTableOptions;
import com.amazonaws.services.dynamodbv2.CreateDynamoDBTableOptions.CreateDynamoDBTableOptionsBuilder;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisAsyncClientBuilder;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.aws.inbound.kinesis.CheckpointMode;
import org.springframework.integration.aws.inbound.kinesis.KinesisMessageDrivenChannelAdapter;
import org.springframework.integration.aws.inbound.kinesis.ListenerMode;
import org.springframework.integration.aws.lock.DynamoDbLockRegistry;
import org.springframework.integration.aws.metadata.DynamoDbMetaDataStore;
import org.springframework.integration.aws.support.AwsHeaders;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.messaging.PollableChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
@EnableIntegration
@IntegrationComponentScan("com.loyalty.consumer")
public class ConsumerClientConfiguration {

  @Value("${cloud.aws.region.static}")
  private String awsRegion;


  @Autowired
  ConsumerClientProperties consumerClientProperties;

  @Bean
  public AmazonKinesis amazonKinesis() {
    AmazonKinesis amazonKinesis;

    System.setProperty(SDKGlobalConfiguration.AWS_CBOR_DISABLE_SYSTEM_PROPERTY, "true");
    String url = consumerClientProperties.getKinesis().getUrl();

    amazonKinesis = AmazonKinesisAsyncClientBuilder.standard()
        .withClientConfiguration(
            new ClientConfiguration()
                .withMaxErrorRetry(consumerClientProperties.getKinesis().getRetries())
                .withConnectionTimeout(
                    consumerClientProperties.getKinesis().getConnectionTimeout()))
        .withEndpointConfiguration(
            new AwsClientBuilder.EndpointConfiguration(
                url,
                Regions.fromName(awsRegion).getName()))
        .build();

    return amazonKinesis;

  }

  @Bean(name = PollerMetadata.DEFAULT_POLLER)
  public PollerMetadata defaultPoller() {

    PollerMetadata pollerMetadata = new PollerMetadata();
    pollerMetadata.setTrigger(new PeriodicTrigger(10));
    return pollerMetadata;
  }


  public AmazonDynamoDBAsync amazonDynamoDB() {
    String url = consumerClientProperties.getDynamoDB().getUrl();
    final AmazonDynamoDBAsync amazonDynamoDB = AmazonDynamoDBAsyncClientBuilder.standard()
        .withEndpointConfiguration(new EndpointConfiguration(
            url,
            Regions.fromName(awsRegion).getName()))
        .withClientConfiguration(new ClientConfiguration()
            .withMaxErrorRetry(consumerClientProperties.getDynamoDB().getRetries())
            .withConnectionTimeout(consumerClientProperties.getDynamoDB().getConnectionTimeout()))
        .build();
    return amazonDynamoDB;
  }

  public DynamoDbMetaDataStore dynamoDbMetaDataStore() {
    DynamoDbMetaDataStore dynamoDbMetaDataStore = new DynamoDbMetaDataStore(amazonDynamoDB(),
        consumerClientProperties.getName());
    return dynamoDbMetaDataStore;
  }


  public KinesisMessageDrivenChannelAdapter kinesisInboundChannel(
      AmazonKinesis amazonKinesis, String[] streamNames) {
    KinesisMessageDrivenChannelAdapter adapter =
        new KinesisMessageDrivenChannelAdapter(amazonKinesis, streamNames);
    adapter.setConverter(null);
    adapter.setOutputChannel(kinesisReceiveChannel());
    adapter.setCheckpointStore(dynamoDbMetaDataStore());
    adapter.setStartTimeout(10000);
    adapter.setConsumerGroup(consumerClientProperties.getName());
    adapter.setCheckpointMode(CheckpointMode.manual);
    adapter.setListenerMode(ListenerMode.batch);
    adapter.setDescribeStreamRetries(1);
    return adapter;
  }

  public PollableChannel kinesisReceiveChannel() {
    QueueChannel queueChannel = new QueueChannel();
    return queueChannel;
  }

  @Bean
  public IntegrationFlow kinesisIntegration() {
    final Set<String> streamNames = consumerClientProperties.getKinesis().getStreams().keySet();
    return IntegrationFlows.from(kinesisInboundChannel(amazonKinesis(),
        streamNames.toArray(new String[streamNames.size()])))
        .route(router())
        .get();
  }


  public HeaderValueRouter router() {
    HeaderValueRouter router = new HeaderValueRouter(AwsHeaders.RECEIVED_STREAM);
    consumerClientProperties.getKinesis().getStreams().forEach((k, v) ->
        router.setChannelMapping(k, v)
    );
    return router;
  }

}
