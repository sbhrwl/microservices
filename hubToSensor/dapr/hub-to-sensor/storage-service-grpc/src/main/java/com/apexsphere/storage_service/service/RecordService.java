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
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public RecordService() {
        this.daprClient = new DaprClientBuilder().build();
        log.info("RecordService initialized. ID generator starting at {}", idGenerator.get());
    }

    /**
     * Save a new record and create initial change log entry.
     */
    public Record saveRecord(Record record) {
        log.info("[saveRecord] Start processing new record: {}", record);

        // Generate numeric ID and assign
        long numericId = idGenerator.incrementAndGet();
        record.setId(String.valueOf(numericId));
        log.info("[saveRecord] Assigned numeric ID: {}", record.getId());

        // Save the record
        String recordKey = RECORD_KEY_PREFIX + numericId;
        log.debug("[saveRecord] Saving record to Dapr state store '{}' with key '{}'", STATE_STORE_NAME, recordKey);
        daprClient.saveState(STATE_STORE_NAME, recordKey, record).block();
        log.info("[saveRecord] Record saved successfully with key '{}'", recordKey);

        // Create initial change log
        RequestChangeLog logEntry = new RequestChangeLog(record.getId(), "Control Requested");
        logEntry.setChangeTimestamp(Instant.now());
        String changeLogKey = CHANGE_LOG_KEY_PREFIX + numericId;
        log.debug("[saveRecord] Saving initial change log entry with key '{}': {}", changeLogKey, logEntry);
        daprClient.saveState(STATE_STORE_NAME, changeLogKey, logEntry).block();
        log.info("[saveRecord] Initial change log saved successfully for key '{}'", changeLogKey);

        log.info("[saveRecord] Completed processing new record with ID '{}'", record.getId());
        return record;
    }

    /**
     * Update record status and append to change log.
     */
    public Record updateRecordStatus(Record updatedRecord) {
        log.info("[updateRecordStatus] Start updating record: {}", updatedRecord);

        String recordId = updatedRecord.getId();
        if (recordId == null || recordId.isEmpty()) {
            log.error("[updateRecordStatus] Record ID is null or empty!");
            throw new RuntimeException("Record ID cannot be null or empty for update.");
        }

        // Retrieve existing record
        String recordKey = RECORD_KEY_PREFIX + recordId;
        log.debug("[updateRecordStatus] Retrieving existing record with key '{}'", recordKey);
        State<Record> state = daprClient.getState(STATE_STORE_NAME, recordKey, Record.class)
                .blockOptional()
                .orElseThrow(() -> {
                    log.error("[updateRecordStatus] Record not found for ID '{}'", recordId);
                    return new RuntimeException("Record not found for update.");
                });

        Record existingRecord = state.getValue();
        log.info("[updateRecordStatus] Existing record retrieved: {}", existingRecord);

        // Update status
        existingRecord.setStatus(updatedRecord.getStatus());
        log.debug("[updateRecordStatus] Updating record status to '{}'", updatedRecord.getStatus());
        daprClient.saveState(STATE_STORE_NAME, recordKey, existingRecord).block();
        log.info("[updateRecordStatus] Record status updated successfully for key '{}'", recordKey);

        // Add change log entry
        RequestChangeLog logEntry = new RequestChangeLog(recordId, "Status updated to " + updatedRecord.getStatus());
        logEntry.setChangeTimestamp(Instant.now());
        String logKey = CHANGE_LOG_KEY_PREFIX + recordId + "_log_" + System.currentTimeMillis();
        log.debug("[updateRecordStatus] Saving change log entry with key '{}': {}", logKey, logEntry);
        daprClient.saveState(STATE_STORE_NAME, logKey, logEntry).block();
        log.info("[updateRecordStatus] Change log entry saved successfully for key '{}'", logKey);

        log.info("[updateRecordStatus] Completed updating record ID '{}'", recordId);
        return existingRecord;
    }
}
