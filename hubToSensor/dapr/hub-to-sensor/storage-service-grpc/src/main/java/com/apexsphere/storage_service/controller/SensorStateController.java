package com.apexsphere.storage_service.controller;

import com.apexsphere.storage_service.service.dapr.DaprRecordService.SensorState;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sensor")
public class SensorStateController {

    private static final Logger log = LoggerFactory.getLogger(SensorStateController.class);
    private static final String STATE_STORE_NAME = "postgres-statestore";

    private final DaprClient daprClient;

    public SensorStateController() {
        this.daprClient = new DaprClientBuilder().build();
    }

    /**
     * GET /sensor/{sensorId}
     * Returns the SensorState stored in Dapr state store.
     */
    @GetMapping("/{sensorId}")
    public Mono<SensorState> getSensorState(@PathVariable String sensorId) {
        if (sensorId == null || sensorId.isBlank()) {
            return Mono.empty();
        }

        final String key = "sensor:" + sensorId.trim();

        log.info("[controller:get] Fetching sensor state for {}", sensorId);

        return daprClient.getState(STATE_STORE_NAME, key, SensorState.class)
                .map(state -> state.getValue());
    }
}
