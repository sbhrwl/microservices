package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.model.MessagePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.client.DaprClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);
    private final DaprClient daprClient;
    private final ObjectMapper objectMapper;

    @Value("${messaging.dapr.pubsub-name}")
    private String pubsubName;

    @Value("${messaging.dapr.request-topic}")
    private String requestTopic;

    @Value("${messaging.dapr.response-topic}")
    private String responseTopic;

    public MessagePublisher(DaprClient daprClient, ObjectMapper objectMapper) {
        this.daprClient = daprClient;
        this.objectMapper = objectMapper;
    }

    public void publishToRequestQueue(MessagePayload payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            log.info("📢 Publishing message to topic {}. JSON Payload: {}", requestTopic, jsonPayload);

            daprClient.publishEvent(pubsubName, requestTopic, payload).block();

            log.info("✅ Message successfully published to Dapr pub/sub.");
        } catch (Exception e) {
            log.error("❌ Failed to publish message via Dapr: {}", e.getMessage(), e);
        }
    }

    public void publishToResponseQueue(MessagePayload payload) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            log.info("📢 Publishing response message to topic {}. JSON Payload: {}", responseTopic, jsonPayload);

            daprClient.publishEvent(pubsubName, responseTopic, payload).block();

            log.info("✅ Response message successfully published to Dapr pub/sub.");
        } catch (Exception e) {
            log.error("❌ Failed to publish response message via Dapr: {}", e.getMessage(), e);
        }
    }
}