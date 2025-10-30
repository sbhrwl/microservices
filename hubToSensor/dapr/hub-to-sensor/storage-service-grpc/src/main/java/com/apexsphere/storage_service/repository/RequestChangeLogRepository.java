package com.apexsphere.storage_service.repository;

import com.apexsphere.storage_service.model.RequestChangeLog;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class RequestChangeLogRepository {

    private static final String STATE_STORE_NAME = "statestore"; // must match your Dapr component name
    private final DaprClient daprClient;

    public RequestChangeLogRepository() {
        this.daprClient = new DaprClientBuilder().build();
    }

    public RequestChangeLog save(RequestChangeLog logEntry) {
        if (logEntry.getId() == null || logEntry.getId().isEmpty()) {
            logEntry.setId(UUID.randomUUID().toString());
        }
        daprClient.saveState(STATE_STORE_NAME, logEntry.getId(), logEntry).block();
        return logEntry;
    }

    public Optional<RequestChangeLog> findById(String id) {
        return Optional.ofNullable(
            daprClient.getState(STATE_STORE_NAME, id, RequestChangeLog.class).block().getValue()
        );
    }

    public void deleteById(String id) {
        daprClient.deleteState(STATE_STORE_NAME, id).block();
    }
}
