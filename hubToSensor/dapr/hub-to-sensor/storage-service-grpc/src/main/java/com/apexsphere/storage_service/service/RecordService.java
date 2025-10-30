package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.model.RequestChangeLog;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.State;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RecordService {

    private static final String STATE_STORE_NAME = "statestore";
    private static final String RECORD_KEY_PREFIX = "record:";
    private static final String CHANGE_LOG_KEY_PREFIX = "requestchangelog:";

    private final DaprClient daprClient;

    public RecordService() {
        this.daprClient = new DaprClientBuilder().build();
    }

    public Record saveRecord(Record record) {
        String recordId = UUID.randomUUID().toString();
        record.setId(recordId);

        daprClient.saveState(STATE_STORE_NAME, RECORD_KEY_PREFIX + recordId, record).block();

        RequestChangeLog log = new RequestChangeLog(
                recordId,
                "Control Requested"
        );
        log.setChangeTimestamp(Instant.now());
        daprClient.saveState(STATE_STORE_NAME, CHANGE_LOG_KEY_PREFIX + UUID.randomUUID(), log).block();

        return record;
    }

    public Record updateRecordStatus(Record updatedRecord) {
        String recordId = updatedRecord.getId();
        if (recordId == null || recordId.isEmpty()) {
            throw new RuntimeException("Record ID cannot be null or empty for update.");
        }

        // Corrected: get State<Record> and extract value
        State<Record> state = daprClient.getState(STATE_STORE_NAME, RECORD_KEY_PREFIX + recordId, Record.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Record not found for update."));

        Record existingRecord = state.getValue();

        existingRecord.setStatus(updatedRecord.getStatus());

        daprClient.saveState(STATE_STORE_NAME, RECORD_KEY_PREFIX + recordId, existingRecord).block();

        RequestChangeLog log = new RequestChangeLog(
                recordId,
                "Status updated to " + updatedRecord.getStatus()
        );
        log.setChangeTimestamp(Instant.now());
        daprClient.saveState(STATE_STORE_NAME, CHANGE_LOG_KEY_PREFIX + UUID.randomUUID(), log).block();

        return existingRecord;
    }
}
