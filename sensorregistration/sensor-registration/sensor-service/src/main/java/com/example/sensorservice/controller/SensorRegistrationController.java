package com.example.sensorservice.controller;

import com.example.sensorservice.dto.SensorRegistrationRequest;
import com.example.sensorservice.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class SensorRegistrationController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/sensor")
    public ResponseEntity<String> registerSensor(@RequestBody SensorRegistrationRequest request) {
        try {
            String message = objectMapper.writeValueAsString(request);
            kafkaProducerService.sendSensorRegistrationMessage(message);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Sensor registration request received and sent to Kafka.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing registration request: " + e.getMessage());
        }
    }
}