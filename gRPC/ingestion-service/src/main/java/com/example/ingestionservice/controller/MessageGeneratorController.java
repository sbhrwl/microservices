package com.example.ingestionservice.controller;

import com.example.ingestionservice.model.RegistrationRequestPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MessageGeneratorController exposes a REST endpoint to manually send
 * RegistrationRequestPojo messages to the ActiveMQ queue. This is useful
 * for testing the ActiveMQ consumption part of the Ingestion Service.
 */
@RestController
@RequestMapping("/generate")
public class MessageGeneratorController {

    private static final Logger logger = LoggerFactory.getLogger(MessageGeneratorController.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${spring.activemq.queue-name}")
    private String queueName;

    /**
     * POST endpoint to send a RegistrationRequestPojo as a JSON message
     * to the configured ActiveMQ queue.
     *
     * @param registrationRequestPojo The POJO representing the sensor registration details.
     * @return A ResponseEntity indicating success or failure.
     */
    @PostMapping("/registration")
    public ResponseEntity<String> generateRegistrationMessage(@RequestBody RegistrationRequestPojo registrationRequestPojo) {
        try {
            logger.info("Attempting to send message to queue '{}': {}", queueName, registrationRequestPojo);
            // Spring's JmsTemplate will automatically convert the POJO to JSON
            // using Jackson (if on classpath) and send it as a TextMessage.
            jmsTemplate.convertAndSend(queueName, registrationRequestPojo);
            logger.info("Successfully sent message to ActiveMQ: {}", registrationRequestPojo);
            return ResponseEntity.ok("Message sent to ActiveMQ successfully.");
        } catch (Exception e) {
            logger.error("Failed to send message to ActiveMQ: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to send message to ActiveMQ: " + e.getMessage());
        }
    }
}
