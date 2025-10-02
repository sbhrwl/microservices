package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.FlexibilityResponse; 
import com.apexsphere.storage_service.service.RecordRequest; 
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class ResponseConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(ResponseConsumer.class);
    
    // 1. Inject the gRPC client directly
    private final RecordGrpcClient grpcClient; 

    @Autowired
    public ResponseConsumer(RecordGrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }
    
    // We specify the containerFactory to ensure this listener uses the XML MessageConverter
    @RabbitListener(
        queues = "${messaging.rabbitmq.response-inbound-queue}",
        containerFactory = "xmlListenerContainerFactory" 
    ) 
    public void handleResponse(FlexibilityResponse response) {
        
        log.info("Received response for RequestID: {}", response.getRequestId());
        log.debug("Full XML response object: {}", response.toString());

        // Determine internal application status based on the received XML
        String internalStatus;

        // --- Core Response Logic & Status Mapping ---
        if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
            internalStatus = "COMPLETED";
            log.info("Request {} succeeded. Message: {}", response.getRequestId(), response.getMessage());
        } else if ("ERROR".equalsIgnoreCase(response.getStatus())) {
            // Include error details in the status field as setMessage/setErrorCode were unavailable in RecordRequest builder
            internalStatus = String.format("FAILED (Code: %s, Msg: %s)", 
                response.getErrorCode(), response.getMessage());
            log.error("Request {} failed with ErrorCode {}. Message: {}", 
                response.getRequestId(), response.getErrorCode(), response.getMessage());
        } else {
            internalStatus = "UNKNOWN";
            log.warn("Request {} returned unknown status: {}", response.getRequestId(), response.getStatus());
        }

        // Call gRPC service to update the status in the DB using the RequestID
        try {
            // 2. Convert the FlexibilityResponse data into the gRPC RecordRequest format
            // This mirrors the use of convertToGrpcRequest in the RequestConsumer.
            RecordRequest updateRequest = convertToGrpcRequest(
                response.getRequestId(), 
                internalStatus
            );

            // 3. Call the gRPC client's update method
            String updateResponse = grpcClient.updateRecordStatus(updateRequest);
            
            log.info("📢 Updated status for RequestID {} to {} via gRPC. Server response: {}", 
                     response.getRequestId(), internalStatus, updateResponse);

        } catch (Exception e) {
            log.error("❌ Failed to update status for RequestID {} in Storage Service: {}", response.getRequestId(), e.getMessage(), e);
        }
    }
    
    /**
     * Converts the response data into the gRPC RecordRequest message for updates.
     * This mimics the structure of the helper method in RequestConsumer.
     * @param requestId The unique ID of the record to update.
     * @param status The new status string (e.g., "COMPLETED" or "FAILED (...)").
     * @return A RecordRequest object ready for gRPC consumption.
     */
    private RecordRequest convertToGrpcRequest(String requestId, String status) {
        // Only set the required fields for an update: RecordId and Status.
        RecordRequest.Builder builder = RecordRequest.newBuilder()
                .setRecordId(requestId) 
                .setStatus(status);
        
        return builder.build();
    }
}
