package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.FlexibilityResponse;
import com.apexsphere.storage_service.service.RecordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Consumer service in the Flexibility Bridge responsible for receiving the JSON response 
 * from the Protocol Adapter, updating the record status, and forwarding the response 
 * back to the final Hub queue.
 *
 * This version consumes the raw JSON string payload and parses it manually 
 * to correctly handle the message type sent by the upstream service.
 */
@RestController
public class ResponseConsumer {

    private static final Logger log = LoggerFactory.getLogger(ResponseConsumer.class);

    private final RecordGrpcClient grpcClient;
    private final ResponseProducerToHub producerToHub;
    private final ObjectMapper objectMapper;

    @Autowired
    public ResponseConsumer(RecordGrpcClient grpcClient,
                            ResponseProducerToHub producerToHub,
                            ObjectMapper objectMapper) {
        this.grpcClient = grpcClient;
        this.producerToHub = producerToHub;
        this.objectMapper = objectMapper;
    }

    /**
     * ✅ Subscribes to the connector's response topic via Dapr.
     *    Consumes messages published by the Protocol Adapter service.
     */
    @Topic(name = "${messaging.dapr.connector-response-topic}", pubsubName = "${messaging.dapr.pubsub-name}")
    @PostMapping(path = "/connector.response")
    public void handleResponse(@RequestBody(required = false) CloudEvent<FlexibilityResponse> cloudEvent) {
        String recordId = "UNKNOWN_ID";

        try {
            if (cloudEvent == null || cloudEvent.getData() == null) {
                log.warn("⚠️ Received empty CloudEvent data — ignoring message.");
                return;
            }

            FlexibilityResponse response = cloudEvent.getData();
            recordId = response.getRequestId();

            log.info("✅ Received and parsed response for RequestID: {}", recordId);
            log.debug("Full parsed response: {}", response);

            // 1️⃣ Update intermediate status via gRPC
            updateRecordStatus(recordId, "Parsed response received from Protocol Adapter");

            // 2️⃣ Forward response to Hub via Dapr
            log.info("📤 Forwarding response for RequestID {} to Hub...", recordId);
            producerToHub.sendResponseToHub(response);

            // 3️⃣ Determine and update final status
            String finalStatus = resolveFinalStatus(response);
            updateRecordStatus(recordId, finalStatus);

            log.info("📢 Final status updated for RequestID {} → {}", recordId, finalStatus);

        } catch (Exception e) {
            log.error("❌ Error processing message for RecordID {}. Cause: {}",
                    recordId, e.getMessage(), e);

            safelyReportErrorToGrpc(recordId, e);

            throw new RuntimeException("Failed to process incoming response message.", e);
        }
    }

    private String resolveFinalStatus(FlexibilityResponse response) {
        if (response == null) {
            return "Request status: UNKNOWN - Null response object";
        }

        if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
            return "Request status: Completed successfully";
        }

        return String.format(
                "Request status: Failed (Code: %s, Message: %s)",
                safe(response.getErrorCode()), safe(response.getMessage()));
    }

    private void updateRecordStatus(String recordId, String status) {
        try {
            RecordRequest request = convertToGrpcRequest(recordId, status);
            String grpcResponse = grpcClient.updateRecordStatus(request);
            log.debug("gRPC status update response: {}", grpcResponse);
        } catch (Exception e) {
            log.error("⚠️ Failed to update record status for ID {}: {}", recordId, e.getMessage());
        }
    }

    private void safelyReportErrorToGrpc(String recordId, Exception e) {
        try {
            String errorStatus = "BRIDGE_JSON_PARSE_FAILED: " + e.getMessage();
            RecordRequest failureRequest = convertToGrpcRequest(recordId, errorStatus);
            grpcClient.updateRecordStatus(failureRequest);
        } catch (Exception inner) {
            log.error("Failed to report processing failure for RecordID {}: {}", recordId, inner.getMessage());
        }
    }

    private RecordRequest convertToGrpcRequest(String requestId, String status) {
        String safeId = (requestId == null || requestId.isBlank()) ? "NO_ID_AVAILABLE" : requestId;
        return RecordRequest.newBuilder()
                .setRecordId(safeId)
                .setStatus(status)
                .build();
    }

    private String safe(String value) {
        return value == null ? "N/A" : value;
    }
}
