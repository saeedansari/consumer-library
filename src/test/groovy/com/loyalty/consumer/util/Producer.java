package com.loyalty.consumer.util;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Producer {

  @Output
  MessageChannel eventOut();

}
