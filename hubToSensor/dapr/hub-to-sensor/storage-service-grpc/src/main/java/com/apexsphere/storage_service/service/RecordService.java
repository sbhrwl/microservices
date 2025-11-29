package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.service.postgres.PostgresRecordService;
import com.apexsphere.storage_service.service.dapr.DaprRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Orchestrates Postgres + optional Dapr persistence with safe reactive handling.
 */
@Service
public class RecordService {

    private static final Logger log = LoggerFactory.getLogger(RecordService.class);

    private final PostgresRecordService postgresRecordService;
    private final DaprRecordService daprRecordService;

    @Value("${storage.enableStateStore:false}")
    private boolean enableStateStore;

    // Queue per sensor to serialize Dapr updates
    private final Map<String, Mono<Void>> sensorUpdateQueue = new ConcurrentHashMap<>();

    public RecordService(PostgresRecordService postgresRecordService,
                         DaprRecordService daprRecordService) {
        this.postgresRecordService = postgresRecordService;
        this.daprRecordService = daprRecordService;
    }

    /**
     * Handles save logic for both Postgres and optional Dapr.
     */
    public RecordResponse handleSave(RecordRequest request) {
        log.info("[RecordService] Saving record. enableStateStore={}", enableStateStore);

        // 1. Save in Postgres (source of truth)
        var postgresResult = postgresRecordService.save(request);

        // 2. Optionally save in Dapr, serialized per sensor
        if (enableStateStore) {
            Mono<Void> daprSaveTask = Mono.fromRunnable(() -> {
                log.info("[DaprRecordService] Sending save to Dapr for sensorId {}", request.getSensorId());
                daprRecordService.save(request)
                        .doOnNext(r -> log.info("[RecordService] Dapr save completed for sensorId {}", request.getSensorId()))
                        .doOnError(e -> log.error("[RecordService] Dapr save failed for sensorId {}: {}", request.getSensorId(), e.getMessage()))
                        .onErrorResume(e -> Mono.empty())
                        .block();
            });

            sensorUpdateQueue.compute(request.getSensorId(), (sensorId, ongoing) -> {
                if (ongoing == null) return daprSaveTask;
                return ongoing.then(daprSaveTask);
            }).doFinally(signal -> sensorUpdateQueue.remove(request.getSensorId()))
              .subscribe();
        }

        // 3. Return Postgres-only response
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
        log.info("[RecordService] Updating record {}. enableStateStore={}", request.getRecordId(), enableStateStore);

        // 1. Update Postgres (source of truth)
        var postgresResult = postgresRecordService.update(request);

        // 2. Optionally update Dapr, serialized per sensor
        if (enableStateStore) {
            Mono<Void> daprUpdateTask = Mono.fromRunnable(() -> {
                log.info("[DaprRecordService] Sending update to Dapr for sensorId {}", request.getSensorId());
                daprRecordService.update(request)
                        .doOnNext(r -> log.info("[RecordService] Dapr update completed for sensorId {}", request.getSensorId()))
                        .doOnError(e -> log.error("[RecordService] Dapr update failed for sensorId {}: {}", request.getSensorId(), e.getMessage()))
                        .onErrorResume(e -> Mono.empty())
                        .block();
            });

            sensorUpdateQueue.compute(request.getSensorId(), (sensorId, ongoing) -> {
                if (ongoing == null) return daprUpdateTask;
                return ongoing.then(daprUpdateTask);
            }).doFinally(signal -> sensorUpdateQueue.remove(request.getSensorId()))
              .subscribe();
        }

        // 3. Return Postgres-only response
        return RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record updated successfully: " + postgresResult.getStatus())
                .setRecordId(String.valueOf(postgresResult.getId()))
                .build();
    }
}
