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

@Service
public class RecordService {

    private static final Logger log = LoggerFactory.getLogger(RecordService.class);

    private final PostgresRecordService postgresRecordService;
    private final DaprRecordService daprRecordService;

    @Value("${storage.enableStateStore:false}")
    private boolean enableStateStore;

    private final Map<String, Mono<Void>> sensorUpdateQueue = new ConcurrentHashMap<>();

    public RecordService(PostgresRecordService postgresRecordService,
                         DaprRecordService daprRecordService) {
        this.postgresRecordService = postgresRecordService;
        this.daprRecordService = daprRecordService;
    }

    public RecordResponse handleSave(RecordRequest request) {
        log.info("[RecordService] Saving record. enableStateStore={}", enableStateStore);

        // Save in Postgres (source of truth)
        var savedRequest = postgresRecordService.save(request);

        // Optionally save in Dapr
        if (enableStateStore) {
            Mono<Void> daprSaveTask = Mono.fromRunnable(() -> {
                log.info("[DaprRecordService] Sending save to Dapr for sensorId {}", savedRequest.getSensorId());
                daprRecordService.save(savedRequest)
                        .doOnNext(r -> log.info("[RecordService] Dapr save completed for sensorId {}", savedRequest.getSensorId()))
                        .doOnError(e -> log.error("[RecordService] Dapr save failed for sensorId {}: {}", savedRequest.getSensorId(), e.getMessage()))
                        .onErrorResume(e -> Mono.empty())
                        .block();
            });

            sensorUpdateQueue.compute(savedRequest.getSensorId(), (sensorId, ongoing) -> {
                if (ongoing == null) return daprSaveTask;
                return ongoing.then(daprSaveTask);
            }).doFinally(signal -> sensorUpdateQueue.remove(savedRequest.getSensorId()))
              .subscribe();
        }

        return RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record saved successfully with ID: " + savedRequest.getRecordId())
                .setRecordId(savedRequest.getRecordId())
                .build();
    }

    public RecordResponse handleUpdate(RecordRequest request) {
        log.info("[RecordService] Updating record {}. enableStateStore={}", request.getRecordId(), enableStateStore);

        // Update Postgres and get a RecordRequest with sensorId populated
        var updatedRequest = postgresRecordService.update(request);

        if (enableStateStore) {
            Mono<Void> daprUpdateTask = Mono.fromRunnable(() -> {
                log.info("[DaprRecordService] Sending update to Dapr for sensorId {}", updatedRequest.getSensorId());
                daprRecordService.update(updatedRequest)
                        .doOnNext(r -> log.info("[RecordService] Dapr update completed for sensorId {}", updatedRequest.getSensorId()))
                        .doOnError(e -> log.error("[RecordService] Dapr update failed for sensorId {}: {}", updatedRequest.getSensorId(), e.getMessage()))
                        .onErrorResume(e -> Mono.empty())
                        .block();
            });

            sensorUpdateQueue.compute(updatedRequest.getSensorId(), (sensorId, ongoing) -> {
                if (ongoing == null) return daprUpdateTask;
                return ongoing.then(daprUpdateTask);
            }).doFinally(signal -> sensorUpdateQueue.remove(updatedRequest.getSensorId()))
              .subscribe();
        }

        return RecordResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Record updated successfully: " + updatedRequest.getStatus())
                .setRecordId(updatedRequest.getRecordId())
                .build();
    }
}
