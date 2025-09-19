package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.service.RecordServiceGrpc; // The generated class
import com.apexsphere.storage_service.service.RecordRequest; // The generated message
import com.apexsphere.storage_service.service.RecordResponse; // The generated message
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class RecordGrpcServiceImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private final RecordService recordService; // Inject your existing service

    @Autowired
    public RecordGrpcServiceImpl(RecordService recordService) {
        this.recordService = recordService;
    }

    @Override
    public void saveRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        try {
            // Map the gRPC request to your existing domain model
            Record record = new Record(
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(),
                request.getDuration(),
                request.getStatus()
            );

            // Call the existing business logic method
            Record savedRecord = recordService.saveRecord(record);

            // Build and send the gRPC response
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record saved successfully with ID: " + savedRecord.getId())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            // Handle any exceptions during the process
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to save record: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}