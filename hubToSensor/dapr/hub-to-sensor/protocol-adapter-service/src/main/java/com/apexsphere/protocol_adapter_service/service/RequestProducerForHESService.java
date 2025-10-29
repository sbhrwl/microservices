package com.apexsphere.protocol_adapter_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing the XML-converted request payload 
 * to the HES system's request queue via the shared exchange.
 */
@Service
public class RequestProducerForHESService {

    private static final Logger log = LoggerFactory.getLogger(RequestProducerForHESService.class);

    private final DaprClient daprClient;

    // The shared exchange used for all routing
    @Value("${messaging.dapr.pubsub-name}")
    private String pubsubName;

    // The routing key specific for HES requests (e.g., "hes.request")
    // NOTE: Changed property name to the request outbound key.
    @Value("${messaging.dapr.hes-request-topic}")
    private String hesRequestTopic;

    public RequestProducerForHESService() {
        this.daprClient = new DaprClientBuilder().build();
    }

    /**
     * Publishes the XML request string to the HES system's request queue.
     * @param xmlPayload The request payload converted to XML string.
     * @param recordId The unique tracing ID for logging.
     */
    public void sendRequestToHes(String xmlPayload, String recordId) {
        // NOTE: The temporary workaround logic to inject <RequestID> has been removed, 
        // as the ProtocolConverter is now confirmed to generate the correct XML structure.
        
        try {
            log.debug("Publishing XML request for Record ID {} via Dapr topic '{}'\nXML Payload:\n{}", 
                      recordId, hesRequestTopic, xmlPayload);
            daprClient.publishEvent(pubsubName, hesRequestTopic, xmlPayload).block();
            log.info("📢 Successfully published HES request for Record ID: {}", recordId);
        } catch (Exception e) {
            log.error("❌ Failed to publish HES request for Record ID {}: {}", recordId, e.getMessage(), e);
            throw new RuntimeException("Failed to send message via Dapr.", e);
        }
    }
}
