package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.storage_service.service.RecordRequest;
import com.apexsphere.storage_service.service.RecordResponse;
import com.apexsphere.storage_service.service.RecordServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
public class RecordGrpcClient {

    private final ManagedChannel channel;
    private final RecordServiceGrpc.RecordServiceBlockingStub blockingStub;

    public RecordGrpcClient() {
        // Build channel manually
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext() // plaintext for dev
                .build();

        this.blockingStub = RecordServiceGrpc.newBlockingStub(channel);
    }

    public String saveRecord(RecordRequest request) {
        RecordResponse response = blockingStub.saveRecord(request);
        return response.getMessage();
    }

    @PreDestroy
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
