package com.apexsphere.storage_service.service.dapr;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.model.RequestChangeLog;
import com.apexsphere.storage_service.service.RecordRequest;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Handles Dapr state store persistence only.
 */
@Service
public class DaprRecordService {

    private static final Logger log = LoggerFactory.getLogger(DaprRecordService.class);

    private static final String STATE_STORE_NAME = "postgres-statestore";
    private static final String RECORD_KEY_PREFIX = "record:";
    private static final String CHANGE_LOG_KEY_PREFIX = "requestchangelog:";

    private final DaprClient daprClient;

    public DaprRecordService() {
        this.daprClient = new DaprClientBuilder().build();
    }

    /**
     * Save record in Dapr state store (fire-and-forget).
     */
    public void save(RecordRequest request, Long postgresId) {
        try {
            String recordKey = RECORD_KEY_PREFIX + postgresId;

            // Convert to Dapr POJO
            Record record = new Record(
                    String.valueOf(postgresId),
                    request.getSensorId(),
                    request.getOperation(),
                    request.getRelayNumber(),
                    request.getDuration(),
                    request.getStatus()
            );

            daprClient.saveState(STATE_STORE_NAME, recordKey, record).block();
            log.info("[DaprRecordService] Record saved in Dapr with key {}", recordKey);

            // Create initial change log
            RequestChangeLog logEntry = new RequestChangeLog(record.getId(), "Control Requested");
            String changeLogKey = CHANGE_LOG_KEY_PREFIX + postgresId;
            daprClient.saveState(STATE_STORE_NAME, changeLogKey, logEntry).block();
            log.info("[DaprRecordService] Initial change log saved in Dapr for key {}", changeLogKey);

        } catch (Exception e) {
            log.error("[DaprRecordService] Failed to save record or change log in Dapr: {}", e.getMessage());
        }
    }

    /**
     * Update record in Dapr state store (fire-and-forget).
     */
    public void update(RecordRequest request) {
        try {
            String recordKey = RECORD_KEY_PREFIX + request.getRecordId();

            // Convert to Dapr POJO
            Record record = new Record(
                    request.getRecordId(),
                    request.getSensorId(),
                    request.getOperation(),
                    request.getRelayNumber(),
                    request.getDuration(),
                    request.getStatus()
            );

            daprClient.saveState(STATE_STORE_NAME, recordKey, record).block();
            log.info("[DaprRecordService] Record updated in Dapr with key {}", recordKey);

            // Add change log
            RequestChangeLog logEntry = new RequestChangeLog(record.getId(),
                    "Status updated to " + request.getStatus());
            String changeLogKey = CHANGE_LOG_KEY_PREFIX + request.getRecordId() + "_log_" + System.currentTimeMillis();
            daprClient.saveState(STATE_STORE_NAME, changeLogKey, logEntry).block();
            log.info("[DaprRecordService] Change log added in Dapr for key {}", changeLogKey);

        } catch (Exception e) {
            log.error("[DaprRecordService] Failed to update record or change log in Dapr: {}", e.getMessage());
        }
    }
}
