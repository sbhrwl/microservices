package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.FlexibilityResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for publishing the final response (JSON format) back to the Hub
 * using the configured response outbound routing key.
 */
@Service
public class ResponseProducerToHub {

    private static final Logger log = LoggerFactory.getLogger(ResponseProducerToHub.class);

    // Using the default RabbitTemplate, configured for JSON conversion.
    private final RabbitTemplate rabbitTemplate; 
    private final String exchangeName;
    private final String responseOutboundRoutingKey;

    public ResponseProducerToHub(
            // Removed @Qualifier to inject the default RabbitTemplate (which uses JSON converter)
            RabbitTemplate rabbitTemplate, 
            @Value("${messaging.rabbitmq.exchange}") String exchangeName,
            @Value("${messaging.rabbitmq.response-outbound-routing-key}") String responseOutboundRoutingKey) {
        
        this.rabbitTemplate = rabbitTemplate;
        this.exchangeName = exchangeName;
        this.responseOutboundRoutingKey = responseOutboundRoutingKey;
    }

    /**
     * Publishes the final FlexibilityResponse object (will be serialized as JSON) back to the hub.
     * This completes the request/response cycle for the bridge service.
     * @param response The response object containing the final status (SUCCESS/ERROR).
     */
    public void sendResponseToHub(FlexibilityResponse response) {
        log.info("Attempting to publish final JSON response object (status: {}) for Request ID {} to Exchange '{}' with Routing Key '{}'",
                 response.getStatus(), response.getRequestId(), exchangeName, responseOutboundRoutingKey);
        
        try {
            // Use the default JSON configured template to convert and send the message
            // The response object is converted to JSON here.
            rabbitTemplate.convertAndSend(exchangeName, responseOutboundRoutingKey, response);
            
            log.info("✅ Successfully published final JSON response for Request ID {} with status {}.", 
                     response.getRequestId(), response.getStatus());
        } catch (Exception e) {
            log.error("❌ Failed to publish response for Request ID {}: {}", 
                      response.getRequestId(), e.getMessage(), e);
            throw new RuntimeException("Failed to send message to RabbitMQ.", e);
        }
    }
}
