package com.example.postservice.controller;

import com.example.postservice.model.CommandRequest;
import com.example.postservice.service.KafkaProducerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commands")
public class CommandController {

    private final KafkaProducerService kafkaProducerService;

    public CommandController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping
    public ResponseEntity<String> handleCommandRequest(@RequestBody CommandRequest commandRequest) {
        System.out.println("Received Command Request: " + commandRequest);
        // Send the command request to Kafka
        kafkaProducerService.sendCommandRequest(commandRequest);

        // Return a success response
        return new ResponseEntity<>("Command sent to Kafka successfully", HttpStatus.OK);
    }
}