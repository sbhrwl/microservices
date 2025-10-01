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
    // NEW: Inject the message producer
    private final MessageProducerService producerService; 

    // Constructor with both dependencies
    public RequestConsumer(RecordGrpcClient grpcClient, MessageProducerService producerService) {
        this.grpcClient = grpcClient;
        this.producerService = producerService;
    }

    // Use the correct inbound queue name from application.yml
    @RabbitListener(queues = "${messaging.rabbitmq.request-inbound-queue}") 
    public void receiveResponse(MessagePayload payload) {
        log.info("✅ Received request for Sensor ID: {}", payload.getSensorId());
        
        try {
            // 1. Save to DB with status: REQUESTED
            RecordRequest saveRequest = convertToGrpcRequest(payload, "REQUESTED");
            String saveResponse = grpcClient.saveRecord(saveRequest);
            
            log.info("➡️ Saved initial record to Storage Service. Status: REQUESTED");
            log.debug("✅ gRPC save successful. Server message: {}", saveResponse);

            // 2. PUBLISH to the Connector queue
            producerService.sendRequestToConnector(payload);
            
            // 3. Update status in DB as SENT (Next Step)
            RecordRequest updateRequest = convertToGrpcRequest(payload, "SENT");
            String updateResponse = grpcClient.updateRecordStatus(updateRequest);
            
            log.info("📢 Updated record status to SENT after publishing message.");
            log.debug("✅ gRPC update successful. Server message: {}", updateResponse);

        } catch (Exception e) {
            log.error("❌ Fatal error in RequestConsumer for Sensor ID {}: {}", 
                      payload.getSensorId(), e.getMessage(), e);
            // Proper AMQP exception handling (e.g., throwing AmqpRejectAndDontRequeueException) 
            // is recommended for production.
        }
    }

    /**
     * Converts the internal MessagePayload object to the gRPC RecordRequest message, 
     * setting the required status.
     */
    private RecordRequest convertToGrpcRequest(MessagePayload payload, String status) {
        return RecordRequest.newBuilder()
                .setSensorId(payload.getSensorId())
                .setOperation(payload.getOperation())
                .setRelayNumber(payload.getRelayNumber())
                .setDuration(payload.getDuration())
                .setStatus(status) // Set the dynamic status
                .build();
    }
}