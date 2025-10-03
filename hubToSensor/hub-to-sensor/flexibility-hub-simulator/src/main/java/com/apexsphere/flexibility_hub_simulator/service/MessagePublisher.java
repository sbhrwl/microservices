package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.config.RabbitMQConfig;
import com.apexsphere.flexibility_hub_simulator.model.MessagePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig config;
    private final ObjectMapper objectMapper;

    public MessagePublisher(RabbitTemplate rabbitTemplate, RabbitMQConfig config, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.config = config;
        this.objectMapper = objectMapper;
    }

    /**
     * Publish a message to the request queue using the request routing key, and log the JSON payload.
     */
    public void publishToRequestQueue(MessagePayload payload) {
        
        try {
            // Log the JSON payload before sending
            String jsonPayload = objectMapper.writeValueAsString(payload);
            log.info("📢 Publishing message to hub {} with routing key {}. JSON Payload: {}", 
                     config.getExchangeName(), 
                     config.getRequestRoutingKey(), 
                     jsonPayload);

            rabbitTemplate.convertAndSend(
                config.getExchangeName(),
                config.getRequestRoutingKey(), // ✅ use request routing key
                payload
            );
            log.info("✅ Message successfully published.");
            
        } catch (Exception e) {
            log.error("❌ Failed to publish message or serialize payload: {}", e.getMessage(), e);
        }
    }

    /**
     * (Optional) Publish a message to the response queue using the response routing key.
     * Useful if the simulator needs to emit responses as well.
     */
    public void publishToResponseQueue(MessagePayload payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            log.info("📢 Publishing response message to hub {} with routing key {}. JSON Payload: {}", 
                     config.getExchangeName(), 
                     config.getResponseRoutingKey(), 
                     jsonPayload);

            rabbitTemplate.convertAndSend(
                config.getExchangeName(),
                config.getResponseRoutingKey(), // ✅ use response routing key
                payload
            );
            log.info("✅ Response message successfully published.");

        } catch (Exception e) {
            log.error("❌ Failed to publish response message or serialize payload: {}", e.getMessage(), e);
        }
    }
}