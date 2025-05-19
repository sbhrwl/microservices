package com.example.sensorservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.sensor-registration}")
    private String sensorRegistrationTopic;

    public void sendSensorRegistrationMessage(String message) {
        kafkaTemplate.send(sensorRegistrationTopic, message);
    }
}