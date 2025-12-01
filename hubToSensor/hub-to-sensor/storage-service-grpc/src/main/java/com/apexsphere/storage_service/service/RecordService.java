package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.service.postgres.PostgresRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RecordService {

    private static final Logger log = LoggerFactory.getLogger(RecordService.class);
    private final PostgresRecordService postgresRecordService;
    public RecordService(PostgresRecordService postgresRecordService) {
        this.postgresRecordService = postgresRecordService;
    }

    public RecordResponse handleSave(RecordRequest request) {
        // Save in Postgres (source of truth)
        var savedRequest = postgresRecordService.save(request);

        return RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record saved successfully with ID: " + savedRequest.getRecordId())
                .setRecordId(savedRequest.getRecordId())
                .build();
    }

    public RecordResponse handleUpdate(RecordRequest request) {
        // Update Postgres and get a RecordRequest with sensorId populated
        var updatedRequest = postgresRecordService.update(request);

        return RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record updated successfully: " + updatedRequest.getStatus())
                .setRecordId(updatedRequest.getRecordId())
                .build();
    }
}