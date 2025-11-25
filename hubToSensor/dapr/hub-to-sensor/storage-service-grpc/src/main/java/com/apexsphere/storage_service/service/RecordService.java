package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.service.postgres.PostgresRecordService;
import com.apexsphere.storage_service.service.dapr.DaprRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Orchestrates Postgres + optional Dapr persistence.
 */
@Service
public class RecordService {

    private static final Logger log = LoggerFactory.getLogger(RecordService.class);

    private final PostgresRecordService postgresRecordService;
    private final DaprRecordService daprRecordService;

    @Value("${storage.enableStateStore:false}")
    private boolean enableStateStore;

    public RecordService(PostgresRecordService postgresRecordService,
                         DaprRecordService daprRecordService) {
        this.postgresRecordService = postgresRecordService;
        this.daprRecordService = daprRecordService;
    }

    /**
     * Handles save logic for both Postgres and (optional) Dapr.
     */
    public RecordResponse handleSave(RecordRequest request) {

        log.info("[RecordService] Saving record. enableStateStore={}", enableStateStore);

        // --- 1. Always save in Postgres (source of truth) ---
        var postgresResult = postgresRecordService.save(request);

        // --- 2. Optionally save in Dapr ---
        if (enableStateStore) {
            try {
                daprRecordService.save(request, postgresResult.getId());
            } catch (Exception e) {
                log.error("[RecordService] Dapr save failed but Postgres succeeded: {}", e.getMessage());
            }
        }

        // --- 3. Generate gRPC response (ONLY from Postgres result) ---
        return RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record saved successfully with ID: " + postgresResult.getId())
                .setRecordId(String.valueOf(postgresResult.getId()))
                .build();
    }

    /**
     * Handles update logic for both Postgres and optional Dapr.
     */
    public RecordResponse handleUpdate(RecordRequest request) {

        log.info("[RecordService] Updating record {}. enableStateStore={}",
                request.getRecordId(), enableStateStore);

        // --- 1. Always update Postgres ---
        var postgresResult = postgresRecordService.update(request);

        // --- 2. Optionally update Dapr ---
        if (enableStateStore) {
            try {
                daprRecordService.update(request);
            } catch (Exception e) {
                log.error("[RecordService] Dapr update failed but Postgres succeeded: {}", e.getMessage());
            }
        }

        // --- 3. Return Postgres-only response ---
        return RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record updated successfully: " + postgresResult.getStatus())
                .setRecordId(String.valueOf(postgresResult.getId()))
                .build();
    }
}
