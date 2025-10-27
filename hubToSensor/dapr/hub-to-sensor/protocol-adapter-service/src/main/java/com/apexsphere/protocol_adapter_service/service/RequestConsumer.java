package com.apexsphere.protocol_adapter_service.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apexsphere.protocol_adapter_service.model.RequestPayload;
import com.apexsphere.storage_service.service.RecordRequest;

@Service
public class RequestConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(RequestConsumer.class); 
    
    private final RecordGrpcClient grpcClient;
    private final RequestProducerForHESService producerService; // RENAMED
    private final ProtocolConverter protocolConverter;

    public RequestConsumer(
            RecordGrpcClient grpcClient, 
            RequestProducerForHESService producerService, // RENAMED
            ProtocolConverter protocolConverter) {
        this.grpcClient = grpcClient;
        this.producerService = producerService;
        this.protocolConverter = protocolConverter;
    }

    @RabbitListener(queues = "${messaging.rabbitmq.request-inbound-queue}") 
    public void receiveResponse(RequestPayload payload) {
        // --- NEW: Retrieve the existing recordId from the incoming payload ---
        String recordId = payload.getRecordId(); 
        
        log.info("✅ Received request for Sensor ID: {} with existing Record ID: {}", payload.getSensorId(), recordId);
        
        if (recordId == null || recordId.isEmpty()) {
            log.error("❌ Incoming RequestPayload is missing a required recordId. Aborting processing.");
            // In a production system, you might throw an exception here to dead-letter the message.
            return;
        }

        try {
            // 1. UPDATE initial status to RECEIVED using the ID from the payload
            RecordRequest initialUpdateRequest = convertToGrpcRequest(payload, "Message recieved for protocol conversion", recordId);
            grpcClient.updateRecordStatus(initialUpdateRequest);
            
            log.info("➡️ Updated initial record status to RECEIVED. ID: {}", recordId);

            // 2. CONVERT JSON to XML
            log.info("🔄 Starting protocol conversion for Record ID: {}", recordId);
            String xmlPayload = protocolConverter.convertPayloadToXml(payload);
            
            // 3. UPDATE status in DB as CONVERSION DONE
            RecordRequest conversionDoneUpdate = convertToGrpcRequest(payload, "Protocol conversion done for request", recordId);
            grpcClient.updateRecordStatus(conversionDoneUpdate);
            log.info("📝 Updated status to 'protocol conversion done' for ID: {}", recordId);
            log.debug("Converted XML payload snippet: {}", xmlPayload.substring(0, Math.min(xmlPayload.length(), 100)));

            // 4. PUBLISH to HES queue using the XML payload
            producerService.sendRequestToHes(xmlPayload, recordId); 
            
            // 5. UPDATE status in DB as SENT TO HES
            RecordRequest sentToHesUpdate = convertToGrpcRequest(payload, "Request sent to HES", recordId);
            String updateResponse = grpcClient.updateRecordStatus(sentToHesUpdate);

            log.info("📢 Updated status to 'Sent to HES' for ID: {}. Publishing successful.", recordId);
            log.debug("✅ gRPC update successful. Server message: {}", updateResponse);

        } catch (Exception e) {
            log.error("❌ Fatal error in RequestConsumer for Sensor ID {} (Record ID {}): {}", 
                      payload.getSensorId(), recordId, e.getMessage(), e);
        }
    }

    /**
     * Converts the internal MessagePayload object to the gRPC RecordRequest message.
     * The recordId is now always expected to be present.
     */
    private RecordRequest convertToGrpcRequest(RequestPayload payload, String status, String recordId) {
        // We now rely on recordId always being passed from the RequestPayload 
        // to convertToGrpcRequest.
        RecordRequest.Builder builder = RecordRequest.newBuilder()
                .setSensorId(payload.getSensorId())
                .setOperation(payload.getOperation())
                .setRelayNumber(payload.getRelayNumber())
                .setDuration(payload.getDuration())
                .setStatus(status);

        // Record ID is required for update operation
        if (recordId != null && !recordId.isEmpty()) {
            builder.setRecordId(recordId);
        }
        
        return builder.build();
    }
}
