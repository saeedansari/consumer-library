package com.loyalty.consumer.configuration;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "loyalty.consumer")
public class ConsumerClientProperties {

  private Kinesis kinesis;
  private DynamoDB dynamoDB;
  private String name;

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setKinesis(Kinesis kinesis) {
    this.kinesis = kinesis;
  }

  public Kinesis getKinesis() {
    return kinesis;
  }

  public void setDynamoDB(DynamoDB dynamoDB) {
    this.dynamoDB = dynamoDB;
  }

  public DynamoDB getDynamoDB() {
    return dynamoDB;
  }

  public static class Kinesis {

    public Kinesis(){}

    Map<String, String> streams = new HashMap<>();
    private int retries;
    private String url;
    private int connectionTimeout;

    public void setStreams(Map<String, String> streams) {
      this.streams = streams;
    }

    public Map<String, String> getStreams() {
      return streams;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }

    public void setConnectionTimeout(int connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
      return connectionTimeout;
    }

    public void setRetries(int retries) {
      this.retries = retries;
    }

    public int getRetries() {
      return retries;
    }

  }

  public static class DynamoDB {
    private int retries;
    private String url;
    private int connectionTimeout;

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }

    public void setConnectionTimeout(int connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
    }

    public int getConnectionTimeout() {
      return connectionTimeout;
    }

    public void setRetries(int retries) {
      this.retries = retries;
    }

    public int getRetries() {
      return retries;
    }

  }





}
