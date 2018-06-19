package com.loyalty.consumer

import com.loyalty.consumer.producer.EventProducer
import com.loyalty.issuance.journal.event.MilesIssuedProtos
import org.springframework.beans.factory.annotation.Autowired

class ConsumerSpecification extends BaseTest {


    @Autowired
    EventProducer eventProducer

    @Autowired
    EventConsumer eventConsumer

    def setup() {
    }

    def "Should receive published msg"() {
        given:
            def msg = "message"

        when:
            eventProducer.produce(msg)

        then:
            eventConsumer.getMessages()
    }

}
