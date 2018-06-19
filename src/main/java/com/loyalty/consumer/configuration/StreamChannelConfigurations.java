package com.loyalty.consumer.configuration;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "loyalty.messaging")
public class StreamChannelConfigurations {

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
