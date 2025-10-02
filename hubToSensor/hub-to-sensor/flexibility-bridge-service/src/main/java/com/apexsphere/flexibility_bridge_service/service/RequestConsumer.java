package com.apexsphere.flexibility_bridge_service.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apexsphere.flexibility_bridge_service.model.MessagePayload;
import com.apexsphere.storage_service.service.RecordRequest;

@Service
public class RequestConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(RequestConsumer.class); 
    
    private final RecordGrpcClient grpcClient;
    private final MessageProducerService producerService; 

    public RequestConsumer(RecordGrpcClient grpcClient, MessageProducerService producerService) {
        this.grpcClient = grpcClient;
        this.producerService = producerService;
    }

    @RabbitListener(queues = "${messaging.rabbitmq.request-inbound-queue}") 
    public void receiveResponse(MessagePayload payload) {
        log.info("✅ Received request for Sensor ID: {}", payload.getSensorId());
        
        String recordId = null; // Variable to store the generated ID

        try {
            // 1. Save to DB with status: REQUESTED
            // For saving, we use the simpler conversion method (no ID needed)
            RecordRequest saveRequest = convertToGrpcRequest(payload, "Control Requested", null);
            
            // grpcClient.saveRecord now returns the generated unique ID (String)
            recordId = grpcClient.saveRecord(saveRequest);
            
            log.info("➡️ Saved initial record to Storage Service. Status: Control Requested. Generated ID: {}", recordId);
            log.debug("✅ gRPC save successful. Record ID returned: {}", recordId);

            // 2. PUBLISH to the Connector queue
            producerService.sendRequestToConnector(payload, recordId);
            
            // 3. Update status in DB as SENT 
            // We use the generated recordId from step 1 for the update request.
            RecordRequest updateRequest = convertToGrpcRequest(payload, "Sent for protocol conversion", recordId);
            String updateResponse = grpcClient.updateRecordStatus(updateRequest);

            log.info("📢 Updated record status to Sent for protocol conversion for request ID: {}. Publishing successful.", recordId);
            log.debug("✅ gRPC update successful. Server message: {}", updateResponse);

        } catch (Exception e) {
            log.error("❌ Fatal error in RequestConsumer for Sensor ID {} (Record ID {}): {}", 
                      payload.getSensorId(), recordId != null ? recordId : "N/A", e.getMessage(), e);
        }
    }

    /**
     * Converts the internal MessagePayload object to the gRPC RecordRequest message.
     * @param payload The original message payload.
     * @param status The status to set (e.g., REQUESTED, SENT).
     * @param recordId The unique ID of the record (required for updates, can be null for saves).
     * @return A RecordRequest object ready for gRPC consumption.
     */
    private RecordRequest convertToGrpcRequest(MessagePayload payload, String status, String recordId) {
        RecordRequest.Builder builder = RecordRequest.newBuilder()
                .setSensorId(payload.getSensorId())
                .setOperation(payload.getOperation())
                .setRelayNumber(payload.getRelayNumber())
                .setDuration(payload.getDuration())
                .setStatus(status);

        // Include the ID only if it is provided (needed for updates)
        if (recordId != null && !recordId.isEmpty()) {
            builder.setRecordId(recordId);
        }
        
        return builder.build();
    }
}
