package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.FlexibilityResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Service responsible for publishing the final response (XML format) back to the Hub
 * using the configured response outbound routing key.
 */
@Service
public class ResponseProducerToHub {

    private static final Logger log = LoggerFactory.getLogger(ResponseProducerToHub.class);

    private final RabbitTemplate xmlRabbitTemplate;
    private final String exchangeName;
    private final String responseOutboundRoutingKey;

    public ResponseProducerToHub(
            // We use @Qualifier to inject the XML-specific template
            @Qualifier("xmlRabbitTemplate") RabbitTemplate xmlRabbitTemplate, 
            @Value("${messaging.rabbitmq.exchange}") String exchangeName,
            @Value("${messaging.rabbitmq.response-outbound-routing-key}") String responseOutboundRoutingKey) {
        
        this.xmlRabbitTemplate = xmlRabbitTemplate;
        this.exchangeName = exchangeName;
        this.responseOutboundRoutingKey = responseOutboundRoutingKey;
    }

    /**
     * Publishes the final FlexibilityResponse (XML format) back to the hub.
     * This completes the request/response cycle for the bridge service.
     * @param response The response object containing the final status (SUCCESS/ERROR).
     */
    public void sendResponseToHub(FlexibilityResponse response) {
        log.info("Attempting to publish final status {} for Request ID {} to Exchange '{}' with Routing Key '{}'",
                 response.getStatus(), response.getRequestId(), exchangeName, responseOutboundRoutingKey);
        
        try {
            // Use the XML configured template to convert and send the message as XML
            xmlRabbitTemplate.convertAndSend(exchangeName, responseOutboundRoutingKey, response);
            
            log.info("✅ Successfully published final response for Request ID {} with status {}.", 
                     response.getRequestId(), response.getStatus());
        } catch (Exception e) {
            log.error("❌ Failed to publish response for Request ID {}: {}", 
                      response.getRequestId(), e.getMessage(), e);
        }
    }
}
