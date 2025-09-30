package com.apexsphere.flexibility_bridge_service.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.apexsphere.flexibility_bridge_service.model.MessagePayload;
import com.apexsphere.storage_service.service.RecordRequest; // Import the generated gRPC Request

@Service
public class RequestConsumer {
    
    // Inject the gRPC client service
    private final RecordGrpcClient grpcClient;

    // Use constructor injection for dependencies
    public RequestConsumer(RecordGrpcClient grpcClient) {
        this.grpcClient = grpcClient;
    }

    @RabbitListener(queues = "${messaging.rabbitmq.request-queue}")
    public void receiveResponse(MessagePayload payload) {
        System.out.println("✅ Received request from external service:");
        System.out.println("Sensor ID: " + payload.getSensorId());
        
        try {
            // 1. Convert the DTO to the gRPC Request
            RecordRequest grpcRequest = convertToGrpcRequest(payload);
            
            // 2. Call the gRPC client service
            String serverResponse = grpcClient.saveRecord(grpcRequest);
            
            System.out.println("➡️ Sending to Storage Service via gRPC...");
            System.out.println("✅ gRPC call successful. Server message: " + serverResponse);

        } catch (Exception e) {
            // Log the error if the gRPC call or conversion fails
            System.err.println("❌ Error processing request or calling gRPC service: " + e.getMessage());
            // In a real application, you would handle message reprocessing/DLQs here
        }
    }

    /**
     * Converts the internal MessagePayload object to the gRPC RecordRequest message.
     */
    private RecordRequest convertToGrpcRequest(MessagePayload payload) {
        // Build the gRPC RecordRequest from the received payload data
        return RecordRequest.newBuilder()
                .setSensorId(payload.getSensorId())
                .setOperation(payload.getOperation())
                .setRelayNumber(payload.getRelayNumber())
                .setDuration(payload.getDuration())
                // Assuming you want to set a default status, as it's required by the proto but not in MessagePayload
                .setStatus("REQUESTED") 
                .build();
    }
}