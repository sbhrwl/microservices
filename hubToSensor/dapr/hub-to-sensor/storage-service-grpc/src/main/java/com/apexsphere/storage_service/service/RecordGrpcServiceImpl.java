package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * gRPC service implementation for the Storage Service using Dapr state store.
 */
@GrpcService
public class RecordGrpcServiceImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private final RecordService recordService;

    @Autowired
    public RecordGrpcServiceImpl(RecordService recordService) {
        this.recordService = recordService;
    }

    /**
     * Handles saving a new record (via gRPC).
     */
    @Override
    public void saveRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        try {
            Record record = new Record(
                null, // ID will be generated (UUID) by service layer
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(),
                request.getDuration(),
                request.getStatus()
            );

            Record savedRecord = recordService.saveRecord(record);

            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record saved successfully with ID: " + savedRecord.getId())
                .setRecordId(savedRecord.getId()) // already String
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to save record: " + e.getMessage())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    /**
     * Handles updating the status of an existing record.
     */
    @Override
    public void updateRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        try {
            Record record = new Record(
                request.getRecordId(),
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(),
                request.getDuration(),
                request.getStatus()
            );

            Record updatedRecord = recordService.updateRecordStatus(record);

            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record status updated to " + updatedRecord.getStatus() + " for ID: " + updatedRecord.getId())
                .setRecordId(updatedRecord.getId())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to update record: " + e.getMessage())
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
