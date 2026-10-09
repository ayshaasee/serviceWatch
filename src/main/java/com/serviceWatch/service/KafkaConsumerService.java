package com.serviceWatch.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(
            topics = "incident-events",
            groupId = "servicewatch-group"
    )
    public void consumeIncidentEvent(String message) {

        System.out.println(
                "KAFKA CONSUMER RECEIVED: " + message
        );
    }
}