package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.config.PubSubConfig;
import com.apexsphere.flexibility_hub_simulator.model.MessagePayload;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile("prod")
public class ResponseConsumerPubSub {

    private final PubSubConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseConsumerPubSub(PubSubConfig config) {
        this.config = config;
    }

    @PostConstruct
    public void startSubscriber() {
        // Create subscription name with projectId and subscriptionId
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(
                config.getProjectId(),
                config.getResponseSubscription()
        );

        // Build the subscriber
        Subscriber subscriber = Subscriber.newBuilder(subscriptionName, this::processMessage).build();

        // Start the subscriber asynchronously
        subscriber.startAsync().awaitRunning();
        System.out.println("✅ ResponseConsumerPubSub started listening to: " + subscriptionName.getSubscription());
    }

    // Method to process incoming messages
    private void processMessage(PubsubMessage message, AckReplyConsumer consumer) {
        try {
            String json = message.getData().toStringUtf8();
            MessagePayload payload = objectMapper.readValue(json, MessagePayload.class);

            System.out.println("✅ Received response from external service:");
            System.out.println("Sensor ID: " + payload.getSensorId());
            System.out.println("Operation: " + payload.getOperation());
            System.out.println("Relay Number: " + payload.getRelayNumber());
            System.out.println("Duration: " + payload.getDuration());
        } catch (Exception e) {
            System.err.println("❌ Failed to parse response message: " + e.getMessage());
        } finally {
            // Acknowledge the message to remove it from the subscription
            consumer.ack();
        }
    }
}
