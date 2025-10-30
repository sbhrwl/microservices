package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.FlexibilityResponse;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Publishes final FlexibilityResponse messages to the Hub.
 * This completes the Bridge → Hub response flow.
 */
@Service
public class ResponseProducerToHub {

    private static final Logger log = LoggerFactory.getLogger(ResponseProducerToHub.class);

    private final DaprClient daprClient;
    private final String pubsubName;
    private final String hubResponseTopic;

    public ResponseProducerToHub(
            @Value("${messaging.dapr.pubsub-name}") String pubsubName,
            @Value("${DAPR_HUB_RESPONSE_TOPIC:flexibility-hub.response}") String hubResponseTopic) {
        this.daprClient = new DaprClientBuilder().build();
        this.pubsubName = pubsubName;
        this.hubResponseTopic = hubResponseTopic;
    }

    /**
     * Publishes the final FlexibilityResponse back to the Hub via Dapr pub/sub.
     *
     * @param response FlexibilityResponse containing requestId, status, and result details.
     */
    public void sendResponseToHub(FlexibilityResponse response) {
        if (response == null) {
            log.error("❌ Attempted to publish a null FlexibilityResponse object. Aborting send operation.");
            return;
        }

        String requestId = safe(response.getRequestId());
        String status = safe(response.getStatus());

        log.info("📤 Publishing final response | RequestID: {} | Status: {} | PubSub: '{}' | Topic: '{}'",
                requestId, status, pubsubName, hubResponseTopic);

        try {
            daprClient.publishEvent(pubsubName, hubResponseTopic, response).block();
            log.info("✅ Successfully published response for RequestID {} (Status: {}).", requestId, status);
        } catch (Exception e) {
            log.error("❌ Failed to publish response for RequestID {}. Error: {}", requestId, e.getMessage(), e);
            throw new RuntimeException("Failed to send message via Dapr for Request ID " + requestId, e);
        }
    }

    private String safe(String value) {
        return (value == null || value.isBlank()) ? "N/A" : value;
    }
}
