package com.apexsphere.protocol_adapter_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing the final JSON response 
 * back to the Bridge/Hub using the response outbound routing key.
 */
@Service
public class ResponseProducerToBridge {

    private static final Logger log = LoggerFactory.getLogger(ResponseProducerToBridge.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchangeName;
    private final String responseRoutingKey;

    public ResponseProducerToBridge(
            RabbitTemplate rabbitTemplate, 
            @Value("${messaging.rabbitmq.exchange}") String exchangeName,
            // Assuming this property holds the key that routes back to the Hub/Bridge (e.g., flexibility-hub.response)
            @Value("${messaging.rabbitmq.response-outbound-routing-key}") String responseRoutingKey) { 
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.responseRoutingKey = responseRoutingKey;
    }

    /**
     * Publishes the final JSON response message back to the Bridge/Hub service.
     * * @param jsonPayload The final message body in JSON format (String).
     * @param recordId The unique ID for logging and tracing purposes.
     */
    public void sendResponseToHub(String jsonPayload, String recordId) {
        log.info("Sending final JSON response (ID: {}) to Exchange: {} with Routing Key: {}", 
                 recordId, exchangeName, responseRoutingKey);
        
        try {
            // Send the JSON string payload
            rabbitTemplate.convertAndSend(exchangeName, responseRoutingKey, jsonPayload);
            log.debug("Successfully published response for ID: {}", recordId);
        } catch (Exception e) {
            log.error("❌ Failed to publish final response (ID: {}). Error: {}", recordId, e.getMessage(), e);
            // Depending on requirements, retry logic or dead-letter queue handling may be added here.
            throw new RuntimeException("Failed to send message to RabbitMQ.", e);
        }
    }
}
