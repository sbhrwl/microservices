package com.apexsphere.protocol_adapter_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing the final JSON response 
 * back to the Bridge/Hub using the response outbound routing key.
 */
@Service
public class ResponseProducerToBridge {

    private static final Logger log = LoggerFactory.getLogger(ResponseProducerToBridge.class);

    private final DaprClient daprClient;
    private final String pubsubName;
    private final String connectorResponseTopic;

    public ResponseProducerToBridge(
            @Value("${messaging.dapr.pubsub-name}") String pubsubName,
            @Value("${messaging.dapr.connector-response-topic}") String connectorResponseTopic) { 
        this.daprClient = new DaprClientBuilder().build();
        this.pubsubName = pubsubName;
        this.connectorResponseTopic = connectorResponseTopic;
    }

    /**
     * Publishes the final JSON response message back to the Bridge/Hub service.
     * @param jsonPayload The final message body in JSON format (String).
     * @param recordId The unique ID for logging and tracing purposes.
     */
    public void sendResponseToHub(String jsonPayload, String recordId) {
        log.info("Sending final JSON response (ID: {}) via Dapr pubsub '{}' topic '{}'", 
                 recordId, pubsubName, connectorResponseTopic);
        
        // --- Added logging for the full JSON payload ---
        log.info("📢 JSON Payload being sent: {}", jsonPayload);
        // ----------------------------------------------
        
        try {
            daprClient.publishEvent(pubsubName, connectorResponseTopic, jsonPayload).block();
            log.debug("Successfully published response for ID: {}", recordId);
        } catch (Exception e) {
            log.error("❌ Failed to publish final response (ID: {}). Error: {}", recordId, e.getMessage(), e);
            throw new RuntimeException("Failed to send message via Dapr.", e);
        }
    }
}
