package com.apexsphere.dataapiservice.repository;

import com.apexsphere.dataapiservice.model.RequestChangeLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import io.dapr.client.domain.State;
import io.dapr.utils.TypeRef;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RequestChangeLogRepository {

    private final DaprClient daprClient;
    private final String stateStoreName;

    public RequestChangeLogRepository(
            @Value("${messaging.dapr.state-store-name}") String stateStoreName) {
        this.daprClient = new DaprClientBuilder().build();
        this.stateStoreName = stateStoreName;
    }

    private String keyForLogs(Long recordId) {
        return "RequestChangeLog:" + recordId;
    }

    public List<RequestChangeLog> findByRecordId(Long recordId) {
        State<List<RequestChangeLog>> state = daprClient.getState(
                stateStoreName,
                keyForLogs(recordId),
                new TypeRef<List<RequestChangeLog>>(){}
        ).block();
        return state != null && state.getValue() != null ? state.getValue() : new ArrayList<>();
    }
}