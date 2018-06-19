package com.loyalty.consumer.events

import com.loyalty.consumer.BaseTest
import com.loyalty.consumer.util.EventConsumer
import com.loyalty.consumer.util.EventProducer
import com.loyalty.issuance.journal.event.MilesIssuedProtos
import org.springframework.beans.factory.annotation.Autowired

class EventFlowSpecification extends BaseTest {


    @Autowired
    EventProducer eventProducer

    @Autowired
    EventConsumer eventConsumer

    def setup() {
    }

    def "Should receive published msg"() {
        given:
            MilesIssuedProtos.MilesIssued issuanceEvent = MilesIssuedProtos.MilesIssued.newBuilder()
                    .setMemberId(80000000021L)
                    .setCardNumber("80000000021")
                    .setIssuerCode("LCBO")
                    .setOfferCode("OFFER1")
                    .build()

        when:
            eventProducer.produce(issuanceEvent)

        then:
            def messages = eventConsumer.getMessages(3000)
            def message = messages.get(0)
            message.cardNumber == issuanceEvent.cardNumber
            message.issuerCode == issuanceEvent.issuerCode
            message.offerCode == issuanceEvent.offerCode
    }

}
