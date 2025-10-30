package com.apexsphere.storage_service.repository;

import com.apexsphere.storage_service.model.Record;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RecordRepository {

    private static final String STATE_STORE_NAME = "statestore"; // must match your Dapr component name
    private final DaprClient daprClient;

    public RecordRepository() {
        this.daprClient = new DaprClientBuilder().build();
    }

    public Record save(Record record) {
        if (record.getId() == null || record.getId().isEmpty()) {
            record.setId(UUID.randomUUID().toString());
        }
        daprClient.saveState(STATE_STORE_NAME, record.getId(), record).block();
        return record;
    }

    public Optional<Record> findById(String id) {
        return Optional.ofNullable(
            daprClient.getState(STATE_STORE_NAME, id, Record.class).block().getValue()
        );
    }

    public void deleteById(String id) {
        daprClient.deleteState(STATE_STORE_NAME, id).block();
    }
}
