package com.loyalty.consumer.configuration

import com.loyalty.consumer.producer.Producer
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBinding(Producer)
class TestConfiguration {

}
