package com.apexsphere.dataapiservice.repository;

import com.apexsphere.dataapiservice.model.ControlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.State;
import io.dapr.utils.TypeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ControlRequestRepository {
    private static final Logger log = LoggerFactory.getLogger(ControlRequestRepository.class);

    private final DaprClient daprClient;
    private final String stateStoreName;
    private final String indexKey;

    public ControlRequestRepository(
            @Value("${messaging.dapr.state-store-name}") String stateStoreName,
            @Value("${messaging.dapr.requests-index-key}") String indexKey) {
        this.daprClient = new DaprClientBuilder().build();
        this.stateStoreName = stateStoreName;
        this.indexKey = indexKey;
    }

    private String keyForId(Long id) {
        return "ControlRequest:" + id;
    }

    public Optional<ControlRequest> findById(Long id) {
        // Try primary key pattern first
        State<ControlRequest> state = daprClient.getState(stateStoreName, keyForId(id), ControlRequest.class).block();
        ControlRequest value = state != null ? state.getValue() : null;
        if (value != null) {
            return Optional.of(value);
        }

        // Fallback 1: plain id as key (e.g., "74")
        State<ControlRequest> plain = daprClient.getState(stateStoreName, String.valueOf(id), ControlRequest.class).block();
        value = plain != null ? plain.getValue() : null;
        if (value != null) {
            return Optional.of(value);
        }

        // Fallback 2: alternative prefixes used by other services
        String[] altPrefixes = new String[] { "Record:", "Request:", "ControlReq:", "CR:" };
        for (String prefix : altPrefixes) {
            State<ControlRequest> alt = daprClient.getState(stateStoreName, prefix + id, ControlRequest.class).block();
            value = alt != null ? alt.getValue() : null;
            if (value != null) {
                return Optional.of(value);
            }
        }

        log.warn("ControlRequest not found in Dapr state. Tried keys: {}, {}, {}",
                keyForId(id), String.valueOf(id), "[Record:, Request:, ControlReq:, CR:] + id");
        return Optional.empty();
    }

    public List<ControlRequest> findAll() {
        // Read an index of IDs, then fetch each
        State<List<Long>> indexState = daprClient.getState(stateStoreName, indexKey, new TypeRef<List<Long>>(){}).block();
        List<ControlRequest> results = new ArrayList<>();
        if (indexState != null && indexState.getValue() != null) {
            for (Long id : indexState.getValue()) {
                findById(id).ifPresent(results::add);
            }
        }
        return results;
    }
}