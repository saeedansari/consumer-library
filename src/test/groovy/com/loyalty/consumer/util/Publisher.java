package com.loyalty.consumer.util;


import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.loyalty.consumer.configuration.ConsumerClientProperties;
import com.loyalty.issuance.journal.event.MilesIssuedProtos;
import com.loyalty.issuance.journal.event.MilesIssuedProtos.MilesIssued;
import java.nio.ByteBuffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Publisher {

  @Value("${cloud.aws.region.static}")
  private String awsRegion;

  @Autowired
  ConsumerClientProperties consumerClientProperties;

  @Autowired
  AmazonKinesis amazonKinesis;

  public Publisher() {
  }

  public void produce(MilesIssuedProtos.MilesIssued event) {
    final PutRecordRequest record = createRecord(event);
    final PutRecordResult putRecordResult = amazonKinesis.putRecord(record);
    System.out.println(putRecordResult.getSequenceNumber());
  }


  private PutRecordRequest createRecord(MilesIssued event) {
    PutRecordRequest putRecord = new PutRecordRequest();
    putRecord.setStreamName("mystream");
    putRecord.setPartitionKey("1");
    final byte[] bytes = event.toByteArray();
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    putRecord.setData(buffer);
    return putRecord;
  }

}
