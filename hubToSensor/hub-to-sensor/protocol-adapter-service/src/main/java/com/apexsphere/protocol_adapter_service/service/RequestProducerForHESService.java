package com.apexsphere.protocol_adapter_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing the XML-converted request payload 
 * to the HES system's request queue via the shared exchange.
 */
@Service
public class RequestProducerForHESService {

    private static final Logger log = LoggerFactory.getLogger(RequestProducerForHESService.class);

    private final RabbitTemplate rabbitTemplate;

    // The shared exchange used for all routing
    @Value("${messaging.rabbitmq.exchange}")
    private String exchangeName;

    // The routing key specific for HES requests (e.g., "hes.request")
    // NOTE: Changed property name to the request outbound key.
    @Value("${messaging.rabbitmq.request-outbound-routing-key}")
    private String hesRequestRoutingKey;

    public RequestProducerForHESService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes the XML request string to the HES system's request queue.
     * @param xmlPayload The request payload converted to XML string.
     * @param recordId The unique tracing ID for logging.
     */
    public void sendRequestToHes(String xmlPayload, String recordId) {
        try {
            log.debug("Publishing XML request for Record ID {} to Exchange: {} with Routing Key: {}", 
                      recordId, exchangeName, hesRequestRoutingKey);
            
            // Sends the XML string payload
            rabbitTemplate.convertAndSend(exchangeName, hesRequestRoutingKey, xmlPayload);

            log.info("📢 Successfully published HES request for Record ID: {}", recordId);
        } catch (Exception e) {
            log.error("❌ Failed to publish HES request for Record ID {}: {}", recordId, e.getMessage(), e);
            throw new RuntimeException("Failed to send message to HES queue.", e);
        }
    }
}
