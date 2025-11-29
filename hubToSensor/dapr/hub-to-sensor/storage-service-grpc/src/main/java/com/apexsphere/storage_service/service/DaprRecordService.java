package com.apexsphere.storage_service.service.dapr;

import com.apexsphere.storage_service.service.RecordRequest;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
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
        // Using default ObjectMapper internally
        this.daprClient = new DaprClientBuilder().build();
    }

    /**
     * Save a new sensor state only if it does not exist.
     */
    public Mono<Void> save(RecordRequest request) {
        String sensorId = request.getSensorId();
        if (sensorId == null || sensorId.isBlank()) {
            log.warn("[save] SensorId is missing. Skipping Dapr save.");
            return Mono.empty();
        }

        final String key = "sensor:" + sensorId;

        return daprClient.getState(STATE_STORE_NAME, key, SensorState.class)
                .flatMap(stateObj -> {
                    SensorState existing = stateObj.getValue();
                    if (existing != null) {
                        log.info("[save] State already exists for sensorId {}. Skipping save.", sensorId);
                        return Mono.empty();
                    }

                    SensorState state = new SensorState(
                            sensorId,
                            request.getOperation(),
                            sensorId, // placeholder for lastRequestId
                            Instant.now().toString()
                    );
                    log.info("[save] Creating new state for sensorId {} with status {}", state.sensorId, state.status);
                    return daprClient.saveState(STATE_STORE_NAME, key, state);
                });
    }

    /**
     * Update the existing sensor state only if request completed successfully.
     */
    public Mono<Void> update(RecordRequest request) {
        String rawSensorId = request.getSensorId();
        final String sensorId = (rawSensorId == null || rawSensorId.isBlank())
                ? resolveSensorId(request.getRecordId()) // implement this method if needed
                : rawSensorId;

        if (sensorId == null || sensorId.isBlank()) {
            log.warn("[update] Cannot find sensorId for recordId {}. Skipping Dapr update.", request.getRecordId());
            return Mono.empty();
        }

        if (!"Request status: Completed successfully".equalsIgnoreCase(request.getStatus())) {
            log.info("[update] Request not completed successfully for sensorId {}. Skipping update.", sensorId);
            return Mono.empty();
        }

        final String key = "sensor:" + sensorId;

        return daprClient.getState(STATE_STORE_NAME, key, SensorState.class)
                .flatMap(stateObj -> {
                    SensorState existing = stateObj.getValue();
                    if (existing == null) {
                        log.info("[update] No existing state for sensorId {}. Skipping update.", sensorId);
                        return Mono.empty();
                    }
                    existing.status = request.getOperation();
                    existing.lastRequestId = sensorId; // placeholder
                    existing.lastUpdated = Instant.now().toString();
                    log.info("[update] Updating state for sensorId {} to status {}", existing.sensorId, existing.status);
                    return daprClient.saveState(STATE_STORE_NAME, key, existing);
                });
    }

    /**
     * Strongly typed sensor state for Dapr.
     */
    public static class SensorState {
        public String sensorId;
        public String status;
        public String lastRequestId;
        public String lastUpdated; // changed to String for Dapr serialization

        public SensorState() {} // required for deserialization

        public SensorState(String sensorId, String status, String lastRequestId, String lastUpdated) {
            this.sensorId = sensorId;
            this.status = status;
            this.lastRequestId = lastRequestId;
            this.lastUpdated = lastUpdated;
        }
    }

    /**
     * Dummy method to resolve sensorId from recordId.
     * Replace with your actual logic if needed.
     */
    private String resolveSensorId(String recordId) {
        // Example: lookup from DB or cache
        return "resolved-sensor-" + recordId;
    }
}
