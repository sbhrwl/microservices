package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.FlexibilityResponse; 
import com.apexsphere.storage_service.service.RecordRequest; 
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Consumer service in the Flexibility Bridge responsible for receiving the JSON response 
 * from the Protocol Adapter, updating the record status, and forwarding the response 
 * back to the final Hub queue.
 * * This version consumes the raw JSON string payload and parses it manually 
 * to correctly handle the message type sent by the upstream service.
 */
@Component
public class ResponseConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(ResponseConsumer.class);
    
    // Dependencies
    private final RecordGrpcClient grpcClient; 
    private final ResponseProducerToHub producerToHub;
    private final ObjectMapper objectMapper; // Injected from RabbitMQConfig or another @Configuration

    @Autowired
    public ResponseConsumer(RecordGrpcClient grpcClient, ResponseProducerToHub producerToHub, ObjectMapper objectMapper) {
        this.grpcClient = grpcClient;
        this.producerToHub = producerToHub;
        this.objectMapper = objectMapper;
    }
    
    // Listener now expects the raw JSON string payload to avoid conversion errors.
    @RabbitListener(
        queues = "${messaging.rabbitmq.response-inbound-queue}"
    ) 
    public void handleResponse(String jsonResponsePayload) {
        
        FlexibilityResponse response = null;
        String recordId = "UNKNOWN_ID";

        try {
            // Manual Step: Parse the JSON string into the FlexibilityResponse object
            response = objectMapper.readValue(jsonResponsePayload, FlexibilityResponse.class);
            recordId = response.getRequestId();
            
            log.info("✅ Received and successfully parsed JSON response for RequestID: {}", recordId);
            log.debug("Full Response Object: {}", response.toString());
            
            // 1. Update status: "Parsed response recieved from Protocol adapter"
            RecordRequest parsedUpdate = convertToGrpcRequest(recordId, "Parsed response recieved from Protocol adapter");
            grpcClient.updateRecordStatus(parsedUpdate);
            log.info("➡️ Updated status to 'Parsed response recieved from Protocol adapter' for ID: {}", recordId);

            // 2. Publish the object back to the Hub (will be converted to JSON by the producer)
            log.info("Attempting to publish final response (JSON object) for RequestID {} back to Hub...", recordId);
            producerToHub.sendResponseToHub(response);
            
            // 3. Determine Final Status and Update
            String finalStatus;
            if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
                finalStatus = "Request status: Completed successfully";
                log.info("Request {} completed successfully.", recordId);
            } else {
                 finalStatus = String.format("Request staus: Failed (Code: %s, Msg: %s)", 
                    response.getErrorCode(), response.getMessage());
                log.error("Request {} failed. Final status: {}", recordId, finalStatus);
            }
            
            RecordRequest finalUpdate = convertToGrpcRequest(recordId, finalStatus);
            String updateResponse = grpcClient.updateRecordStatus(finalUpdate);
            
            log.info("📢 Final status updated for RequestID {} to {}. Server response: {}", 
                     recordId, finalStatus, updateResponse);

        } catch (Exception e) {
            log.error("❌ Fatal error in Bridge ResponseConsumer for Record ID {}: Failed to parse JSON or process message. Raw Payload: {}. Error: {}", 
                recordId, jsonResponsePayload, e.getMessage(), e);
            
            // Attempt to update status to indicate failure in bridge/adapter if ID is known
            try {
                String errorStatus = "BRIDGE_JSON_PARSE_FAILED: " + e.getMessage();
                RecordRequest failureUpdate = convertToGrpcRequest(recordId, errorStatus);
                grpcClient.updateRecordStatus(failureUpdate);
            } catch (Exception grpcE) {
                log.error("Failed to report fatal error via gRPC for ID {}: {}", recordId, grpcE.getMessage());
            }
            // Re-throw the exception to trigger RabbitMQ's error handling strategy (rejection/DLQ)
            throw new RuntimeException("Failed to process incoming response message.", e);
        }
    }
    
    /**
     * Converts the response data into the gRPC RecordRequest message for updates.
     */
    private RecordRequest convertToGrpcRequest(String requestId, String status) {
        // Ensure we don't try to build a request without a valid ID in case of parsing failure
        if (requestId == null || "UNKNOWN_ID".equals(requestId)) {
            log.warn("Attempted to create gRPC request without a valid record ID. Status: {}", status);
            return RecordRequest.newBuilder().setRecordId("NO_ID_AVAILABLE").setStatus(status).build();
        }
        
        RecordRequest.Builder builder = RecordRequest.newBuilder()
                .setRecordId(requestId) 
                .setStatus(status);
        
        return builder.build();
    }
}
