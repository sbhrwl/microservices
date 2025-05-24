package com.example.sensorservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.sensor-registration}")
    private String sensorRegistrationTopic;

    public void sendSensorRegistrationMessage(String message) {
        logger.info("Sending message to Kafka: {}", message);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(sensorRegistrationTopic, message);
        future.thenAccept(result -> logger.info("Sent sensor registration message with sensorId: {}", message));
    }
}