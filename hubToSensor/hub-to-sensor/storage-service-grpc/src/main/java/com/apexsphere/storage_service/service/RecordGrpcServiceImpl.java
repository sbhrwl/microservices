package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.service.RecordServiceGrpc;
import com.apexsphere.storage_service.service.RecordRequest;
import com.apexsphere.storage_service.service.RecordResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * gRPC service implementation for the Storage Service.
 */
@GrpcService
public class RecordGrpcServiceImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private final RecordService recordService; 

    @Autowired
    public RecordGrpcServiceImpl(RecordService recordService) {
        this.recordService = recordService;
    }

    /**
     * Handles the initial saving of a record.
     * CRITICAL: Returns the database-generated ID to the client for subsequent updates.
     */
    @Override
    public void saveRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        try {
            // Map the gRPC request to your existing domain model
            // NOTE: The request.getRelayNumber() and request.getDuration() return 'int',
            // but the constructor requires 'Integer'. We use Integer.valueOf() to be explicit
            // IF the constructor is not fully matching.
            Record record = new Record(
                (String) null, // ID is null for a new record. String cast remains to match constructor structure.
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(), // Assumed to match the Integer type in model via autoboxing
                request.getDuration(),    // Assumed to match the Integer type in model via autoboxing
                request.getStatus()
            );

            // Call the existing business logic method (which returns the record with the new ID)
            Record savedRecord = recordService.saveRecord(record);

            // Build and send the gRPC response
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record saved successfully with ID: " + savedRecord.getId())
                .setRecordId(String.valueOf(savedRecord.getId())) // <-- FIX: Convert Long ID to String
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
    
    /**
     * Handles updating the status of an existing record.
     */
    @Override
    public void updateRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        try {
            // Map the gRPC request to your existing domain model. 
            // The ID is now correctly supplied by the client for lookup.
            Record record = new Record(
                request.getRecordId(), // <-- CRITICAL: Use the ID (which is a String from gRPC) for lookup
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(),
                request.getDuration(),
                request.getStatus()
            );
            
            // Call the new business logic method to update status
            Record updatedRecord = recordService.updateRecordStatus(record);
            
            // Build and send the gRPC response
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record status updated to " + updatedRecord.getStatus() + " for ID: " + updatedRecord.getId())
                .setRecordId(String.valueOf(updatedRecord.getId())) // <-- FIX: Convert Long ID to String
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            // Handle any exceptions during the process
            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to update record: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
