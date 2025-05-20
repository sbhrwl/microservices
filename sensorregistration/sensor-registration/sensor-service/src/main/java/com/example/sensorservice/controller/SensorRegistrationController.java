package com.example.sensorservice.controller;

import com.example.sensorservice.dto.SensorRegistrationRequest;
import com.example.sensorservice.service.KafkaProducerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;

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
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Sensor registration request received and sent to Kafka.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing registration request: " + e.getMessage());
        }
    }

    @GetMapping("/public")
    public ResponseEntity<String> publicGreeting() {
        LocalTime now = LocalTime.now();
        String greeting;
        if (now.isAfter(LocalTime.of(5, 59)) && now.isBefore(LocalTime.of(12, 0))) {
            greeting = "Good morning";
        } else if (now.isAfter(LocalTime.of(11, 59)) && now.isBefore(LocalTime.of(18, 0))) {
            greeting = "Good evening";
        } else {
            greeting = "Good night";
        }
        return ResponseEntity.ok(greeting);
    }
}