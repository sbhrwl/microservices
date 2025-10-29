package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.FlexibilityResponse;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for publishing the final response (JSON format) back to the Hub
 * using the configured response outbound routing key.
 *
 * This completes the full request-response loop for the Bridge service.
 */
@Service
public class ResponseProducerToHub {

    private static final Logger log = LoggerFactory.getLogger(ResponseProducerToHub.class);

    private final DaprClient daprClient;
    private final String pubsubName;
    private final String hubResponseTopic;

    public ResponseProducerToHub(
            @Value("${messaging.dapr.pubsub-name}") String pubsubName,
            @Value("${messaging.dapr.hub-response-topic}") String hubResponseTopic) {
        this.daprClient = new DaprClientBuilder().build();
        this.pubsubName = pubsubName;
        this.hubResponseTopic = hubResponseTopic;
    }

    /**
     * Publishes the final FlexibilityResponse object (as JSON) back to the Hub via Dapr pub/sub.
     *
     * @param response The final response object containing requestId, status, and result info.
     */
    public void sendResponseToHub(FlexibilityResponse response) {
        if (response == null) {
            log.error("❌ Attempted to publish a null FlexibilityResponse object. Aborting send operation.");
            return;
        }

        String requestId = safe(response.getRequestId());
        String status = safe(response.getStatus());

        log.info("📤 Preparing to publish final JSON response for Request ID: {} | Status: {} | PubSub: '{}' | Topic: '{}'",
                requestId, status, pubsubName, hubResponseTopic);

        try {
            daprClient.publishEvent(pubsubName, hubResponseTopic, response).block();

            log.info("✅ Successfully published final response for Request ID {} (Status: {}).", requestId, status);
        } catch (Exception e) {
            log.error("❌ Failed to publish response for Request ID {}. Error: {}", requestId, e.getMessage(), e);
            throw new RuntimeException("Failed to send message via Dapr for Request ID " + requestId, e);
        }
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }
}
