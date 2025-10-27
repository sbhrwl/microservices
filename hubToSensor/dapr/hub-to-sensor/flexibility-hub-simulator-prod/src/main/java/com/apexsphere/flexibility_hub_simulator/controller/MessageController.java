package com.apexsphere.flexibility_hub_simulator.controller;

import com.apexsphere.flexibility_hub_simulator.model.MessagePayload;
import com.apexsphere.flexibility_hub_simulator.service.MessagePublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessagePublisher publisher;

    public MessageController(MessagePublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody MessagePayload payload) {
        publisher.publishToRequestQueue(payload);
        return ResponseEntity.ok("Message sent to RabbitMQ");
    }
}
