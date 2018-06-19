package com.loyalty.consumer.util

import com.loyalty.issuance.journal.event.MilesIssuedProtos
import org.springframework.integration.annotation.MessageEndpoint
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.Message

@MessageEndpoint
class EventConsumer {


    private List messages = new ArrayList<>()

    @ServiceActivator(inputChannel = "channelA")
    void handleMessage(Message<?> msg) {
        def milesIssued = MilesIssuedProtos.MilesIssued.parseFrom(msg.payload)
        messages.add(milesIssued)
    }


    List getMessages(long delay) {
        def currentTime = System.currentTimeMillis()
        def timeout = currentTime + delay
        while(messages.isEmpty() || timeout > currentTime) {
            currentTime = System.currentTimeMillis()
        }
        return messages
    }

}
