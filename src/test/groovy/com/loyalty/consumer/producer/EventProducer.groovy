package com.loyalty.consumer.producer

import com.loyalty.issuance.journal.event.MilesIssuedProtos
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component

@Component
class EventProducer {

    private Producer producer

    @Autowired
    OrdersSource(Producer producer) {
        this.producer = producer
    }

    void produce(String event) {
        producer.eventOut().send(new GenericMessage<String>(event))
    }


}
