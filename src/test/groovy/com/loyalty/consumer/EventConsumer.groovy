package com.loyalty.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.loyalty.consumer.producer.TestEvent
import com.loyalty.issuance.journal.event.MilesIssuedProtos
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.Message


@MessageEndpoint
class EventConsumer {


    private List messages = new ArrayList<>()

    @ServiceActivator(inputChannel = "channelA")
    void handleMessage(Message msg) {
        messages.add(msg.payload)
        System.out.println("ChannelA " + msg.payload)
    }

    List getMessages() {
        while(messages.isEmpty()) {
        }
        return messages
    }

}
