package com.loyalty.consumer.producer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Producer {

  @Output
  MessageChannel eventOut();

}
