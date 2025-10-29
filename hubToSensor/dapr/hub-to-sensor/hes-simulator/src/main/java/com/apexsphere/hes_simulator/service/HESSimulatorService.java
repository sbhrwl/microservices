package com.apexsphere.hes_simulator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.CloudEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service that simulates the HES (Head-End System) functionality.
 * It consumes XML requests, delays processing for 60 seconds, and sends a random
 * SUCCESS or ERROR XML response back to the protocol adapter.
 */
@RestController
public class HESSimulatorService {

    private static final Logger log = LoggerFactory.getLogger(HESSimulatorService.class);
    
    // Pattern to safely extract the RequestID from the XML string
    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("<RequestID>(\\d+)</RequestID>");
    private static final Random RANDOM = new Random();
    private static final long DELAY_MS = 10; // 60 seconds delay as requested : 60000

    private final DaprClient daprClient;

    @Value("${messaging.dapr.pubsub-name}")
    private String pubsubName;

    // The routing key to send the response back to the Protocol Adapter
    @Value("${messaging.dapr.hes-response-topic}")
    private String responseTopic;

    public HESSimulatorService() {
        this.daprClient = new DaprClientBuilder().build();
    }

    /**
     * Consumes XML request messages via Dapr topic subscription.
     */
    @Topic(name = "${messaging.dapr.hes-request-topic}", pubsubName = "${messaging.dapr.pubsub-name}")
    @PostMapping(path = "/hes.request")
    public void receiveHesRequest(@RequestBody(required = false) CloudEvent<String> cloudEvent) {
        if (cloudEvent == null || cloudEvent.getData() == null) {
            log.warn("⚠️ Received empty CloudEvent data — ignoring message.");
            return;
        }
        String requestXml = cloudEvent.getData();
        // Log the full received request XML payload at DEBUG level
        log.debug("Full received HES Request XML:\n{}", requestXml); 
        
        String requestId = extractRequestId(requestXml);
        log.info("📧 Received HES Request with ID: {}. Starting 60-second simulation delay.", requestId);

        try {
            // 1. Simulate HES processing time (60 seconds)
            Thread.sleep(DELAY_MS);
            
            // 2. Generate random SUCCESS or ERROR response, embedding the original Request ID
            String responseXml = generateRandomResponse(requestId);

            // 3. Send the response back to the protocol adapter via Dapr pub/sub
            daprClient.publishEvent(pubsubName, responseTopic, responseXml).block();

            log.info("✅ HES Response sent for ID: {}. Status: {}", requestId, 
                     responseXml.contains("<Status>SUCCESS</Status>") ? "SUCCESS" : "ERROR");

        } catch (InterruptedException e) {
            // Re-interrupt the thread after catching InterruptedException
            log.error("Simulation interrupted while sleeping for Request ID: {}", requestId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
             log.error("❌ Failed to process or send response for Request ID: {}", requestId, e);
        }
    }

    /**
     * Extracts the RequestID from the incoming XML payload using a simple regex pattern.
     * @param xml The inbound XML string.
     * @return The extracted ID string or "UNKNOWN" if the tag is not found.
     */
    private String extractRequestId(String xml) {
        Matcher matcher = REQUEST_ID_PATTERN.matcher(xml);
        // group(1) captures the content inside the <RequestID> tags
        return matcher.find() ? matcher.group(1) : "UNKNOWN";
    }

    /**
     * Generates a random SUCCESS (70% chance) or ERROR (30% chance) XML response
     * using the provided RequestID and current timestamp.
     * @param requestId The ID to be inserted into the response XML.
     * @return The complete XML response string.
     */
    private String generateRandomResponse(String requestId) {
        // Use ISO_INSTANT for a format like 2025-10-02T10:15:00Z
        String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        boolean isSuccess = RANDOM.nextDouble() < 0.7; // Simulate a 70% success rate

        if (isSuccess) {
            return String.format(
                "<FlexibilityResponse>\n" +
                "<RequestID>%s</RequestID>\n" +
                "<Status>SUCCESS</Status>\n" +
                "<Message>Operation DIRECT-ON for sensor-001 completed successfully.</Message>\n" +
                "<Timestamp>%s</Timestamp>\n" +
                "</FlexibilityResponse>",
                requestId, timestamp
            );
        } else {
            return String.format(
                "<FlexibilityResponse>\n" +
                "<RequestID>%s</RequestID>\n" +
                "<Status>ERROR</Status>\n" +
                "<ErrorCode>404</ErrorCode>\n" +
                "<Message>Target sensor 'sensor-001' not found or offline.</Message>\n" +
                "<Timestamp>%s</Timestamp>\n" +
                "</FlexibilityResponse>",
                requestId, timestamp
            );
        }
    }
}
