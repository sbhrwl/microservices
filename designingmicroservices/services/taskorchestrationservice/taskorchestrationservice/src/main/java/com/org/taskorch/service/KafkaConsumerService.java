package com.org.taskorch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    /**
     * Kafka listener for the 'task.requested' topic.
     * Logs the received messages for monitoring and debugging.
     *
     * @param message The message received from the Kafka topic.
     */
    @KafkaListener(topics = "${spring.kafka.consumer.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTaskRequested(String message) {
        try {
            // Log the received message
            logger.info("Received message from 'task.requested': {}", message);
        } catch (Exception e) {
            logger.error("Error processing message from 'task.requested': {}", e.getMessage(), e);
        }
    }
}