package com.apexsphere.dataapiservice.repository;

import com.apexsphere.dataapiservice.model.ControlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.State;
import io.dapr.utils.TypeRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ControlRequestRepository {

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
        State<ControlRequest> state = daprClient.getState(stateStoreName, keyForId(id), ControlRequest.class).block();
        return Optional.ofNullable(state != null ? state.getValue() : null);
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