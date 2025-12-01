package com.apexsphere.storage_service.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * gRPC service implementation for the Storage Service.
 * Delegates all business logic to RecordService.
 */
@GrpcService
public class RecordGrpcServiceImpl extends RecordServiceGrpc.RecordServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(RecordGrpcServiceImpl.class);

    private final RecordService recordService;

    public RecordGrpcServiceImpl(RecordService recordService) {
        this.recordService = recordService;
        log.info("RecordGrpcServiceImpl initialized.");
    }

    @Override
    public void saveRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        log.info("[saveRecord gRPC] Incoming request: {}", request);

        try {
            // Service handles everything (Postgres + optional Dapr)
            RecordResponse response = recordService.handleSave(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[saveRecord gRPC] Error: {}", e.getMessage(), e);

            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to save record: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void updateRecord(RecordRequest request, StreamObserver<RecordResponse> responseObserver) {
        log.info("[updateRecord gRPC] Incoming request: {}", request);

        if (request.getRecordId() == null || request.getRecordId().isEmpty()) {
            log.warn("[updateRecord gRPC] Missing record_id");
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("Mandatory field 'record_id' is missing.")
                    .asRuntimeException()
            );
            return;
        }

        try {
            // Service handles both Postgres + optional Dapr
            RecordResponse response = recordService.handleUpdate(request);

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("[updateRecord gRPC] Error: {}", e.getMessage(), e);

            RecordResponse response = RecordResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to update record: " + e.getMessage())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}