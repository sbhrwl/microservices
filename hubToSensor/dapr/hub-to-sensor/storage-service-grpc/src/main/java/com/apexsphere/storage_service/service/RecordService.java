package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.model.RequestChangeLog;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RecordService {

    private static final Logger log = LoggerFactory.getLogger(RecordService.class);

    private static final String STATE_STORE_NAME = "postgres-statestore";
    private static final String RECORD_KEY_PREFIX = "record:";
    private static final String CHANGE_LOG_KEY_PREFIX = "requestchangelog:";

    private final DaprClient daprClient;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis()); // pseudo auto-increment

    public RecordService() {
        this.daprClient = new DaprClientBuilder().build();
        log.info("RecordService initialized. Starting ID generator at {}", idGenerator.get());
    }

    /**
     * Save a new record and create initial change log entry.
     */
    public Record saveRecord(Record record) {
        log.info("=== saveRecord called ===");
        log.info("Incoming record payload: {}", record);

        // Generate numeric ID and assign
        long numericId = idGenerator.incrementAndGet();
        record.setId(String.valueOf(numericId));
        log.info("Generated numeric ID for new record: {}", record.getId());

        // Save the record to Postgres via Dapr
        log.info("Saving record to Dapr state store '{}' with key '{}'", STATE_STORE_NAME, RECORD_KEY_PREFIX + numericId);
        daprClient.saveState(STATE_STORE_NAME, RECORD_KEY_PREFIX + numericId, record).block();
        log.info("Record saved successfully.");

        // Add initial change log
        RequestChangeLog logEntry = new RequestChangeLog(
                record.getId(),
                "Control Requested"
        );
        logEntry.setChangeTimestamp(Instant.now());
        log.info("Saving initial change log entry for record {}: {}", record.getId(), logEntry);
        daprClient.saveState(STATE_STORE_NAME, CHANGE_LOG_KEY_PREFIX + numericId, logEntry).block();
        log.info("Initial change log saved successfully.");

        log.info("=== saveRecord completed. Returning record with ID: {} ===", record.getId());
        return record;
    }

    /**
     * Update record status and append to change log.
     */
    public Record updateRecordStatus(Record updatedRecord) {
        log.info("=== updateRecordStatus called ===");
        log.info("Incoming update payload: {}", updatedRecord);

        String recordId = updatedRecord.getId();
        if (recordId == null || recordId.isEmpty()) {
            log.error("Record ID cannot be null or empty for update.");
            throw new RuntimeException("Record ID cannot be null or empty for update.");
        }

        // Retrieve existing record
        log.info("Retrieving existing record from Dapr state store '{}' with key '{}'", STATE_STORE_NAME, RECORD_KEY_PREFIX + recordId);
        State<Record> state = daprClient.getState(STATE_STORE_NAME, RECORD_KEY_PREFIX + recordId, Record.class)
                .blockOptional()
                .orElseThrow(() -> {
                    log.error("Record not found for update. Record ID: {}", recordId);
                    return new RuntimeException("Record not found for update.");
                });

        Record existingRecord = state.getValue();
        log.info("Existing record retrieved: {}", existingRecord);

        // Update status
        existingRecord.setStatus(updatedRecord.getStatus());
        log.info("Updating record status to '{}'", updatedRecord.getStatus());
        daprClient.saveState(STATE_STORE_NAME, RECORD_KEY_PREFIX + recordId, existingRecord).block();
        log.info("Record updated successfully in state store.");

        // Add to change log
        RequestChangeLog logEntry = new RequestChangeLog(
                recordId,
                "Status updated to " + updatedRecord.getStatus()
        );
        logEntry.setChangeTimestamp(Instant.now());
        String logKey = CHANGE_LOG_KEY_PREFIX + recordId + "_log_" + System.currentTimeMillis();
        log.info("Saving change log entry with key '{}': {}", logKey, logEntry);
        daprClient.saveState(STATE_STORE_NAME, logKey, logEntry).block();
        log.info("Change log entry saved successfully.");

        log.info("=== updateRecordStatus completed for record ID: {} ===", recordId);
        return existingRecord;
    }
}
