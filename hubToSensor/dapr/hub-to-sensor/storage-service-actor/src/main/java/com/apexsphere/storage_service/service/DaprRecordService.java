package com.apexsphere.storage_service.service.dapr;

import com.apexsphere.storage_service.service.RecordRequest;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class DaprRecordService {

    private static final Logger log = LoggerFactory.getLogger(DaprRecordService.class);
    private static final String STATE_STORE_NAME = "postgres-statestore";

    private final DaprClient daprClient;

    public DaprRecordService() {
        this.daprClient = new DaprClientBuilder().build();
    }

    /**
     * Save a new sensor state only if it does not exist.
     */
    public Mono<Void> save(RecordRequest request) {
        if (request == null || request.getSensorId() == null || request.getSensorId().isBlank()) {
            log.warn("[save] SensorId missing — skipping save.");
            return Mono.empty();
        }

        final String sensorId = request.getSensorId().trim();
        final String key = "sensor:" + sensorId;

        return daprClient.getState(STATE_STORE_NAME, key, SensorState.class)
                .flatMap(stateObj -> {
                    if (stateObj.getValue() != null) {
                        log.info("[save] Sensor {} already exists — skipping save.", sensorId);
                        return Mono.empty();
                    }

                    SensorState newState = new SensorState(
                            sensorId,
                            request.getOperation(),
                            request.getRecordId(),
                            Instant.now().toString()
                    );

                    log.info("[save] Creating initial state for sensor {} with operation {}", sensorId, newState.status);
                    return daprClient.saveState(STATE_STORE_NAME, key, newState);
                });
    }

    /**
     * Update existing state ONLY using sensorId (never recordId lookup in Dapr).
     * Logs all RecordRequest properties for full visibility.
     */
    public Mono<Void> update(RecordRequest request) {
        if (request == null) {
            log.warn("[update] Request is null — skipping update.");
            return Mono.empty();
        }

        // Log all properties of RecordRequest
        log.info("[update] RecordRequest details: recordId={}, sensorId={}, operation={}, relayNumber={}, duration={}, status={}",
                request.getRecordId(),
                request.getSensorId(),
                request.getOperation(),
                request.getRelayNumber(),
                request.getDuration(),
                request.getStatus()
        );

        final String sensorId = request.getSensorId();
        if (sensorId == null || sensorId.isBlank()) {
            log.warn("[update] DB did not provide sensorId for recordId {} — skipping update.", request.getRecordId());
            return Mono.empty();
        }

        // Only update if status indicates success
        if (request.getStatus() == null || !request.getStatus().toLowerCase().contains("completed successfully")) {
            log.info("[update] Skipping update — request for sensor {} did not complete successfully: {}", sensorId, request.getStatus());
            return Mono.empty();
        }

        final String key = "sensor:" + sensorId.trim();

        return daprClient.getState(STATE_STORE_NAME, key, SensorState.class)
                .flatMap((State<SensorState> stateObj) -> {
                    SensorState existing = stateObj.getValue();
                    if (existing == null) {
                        log.info("[update] No existing Dapr state for sensor {} — skipping update.", sensorId);
                        return Mono.empty();
                    }

                    existing.status = request.getOperation();
                    existing.lastRequestId = request.getRecordId();
                    existing.lastUpdated = Instant.now().toString();

                    log.info("[update] Updating Dapr state for sensor {} to status {}", sensorId, existing.status);

                    return daprClient.saveState(STATE_STORE_NAME, key, existing);
                });
    }

    /**
     * Dapr state object
     */
    public static class SensorState {
        public String sensorId;
        public String status;
        public String lastRequestId;
        public String lastUpdated;

        public SensorState() {}

        public SensorState(String sensorId, String status, String lastRequestId, String lastUpdated) {
            this.sensorId = sensorId;
            this.status = status;
            this.lastRequestId = lastRequestId;
            this.lastUpdated = lastUpdated;
        }
    }
}
