package com.example.commandorchestrator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@Service
public class KafkaProducer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    private static final String TOPIC = "command-topic";

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    public void sendMessage(String key, byte[] message) {
        logger.info("Sending message to Kafka topic: {} with key: {}", TOPIC, key);
        logger.info("Message payload (Base64 encoded): {}", Base64.getEncoder().encodeToString(message));
        kafkaTemplate.send(TOPIC, key, message);
        logger.info("Message sent successfully for key: {}", key);
    }
}
