package com.apexsphere.storage_service.controller;

import com.apexsphere.storage_service.service.dapr.DaprRecordService.SensorState;
import io.dapr.client.DaprClient;
import io.dapr.client.DaprClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
     * Returns 404 if not found.
     */
    @GetMapping("/{sensorId}")
    public Mono<SensorState> getSensorState(@PathVariable String sensorId) {
        if (sensorId == null || sensorId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensorId is missing");
        }

        final String key = "sensor:" + sensorId.trim();

        log.info("[controller:get] Fetching sensor state for {}", sensorId);

        return daprClient.getState(STATE_STORE_NAME, key, SensorState.class)
                .flatMap(state -> {
                    SensorState val = state.getValue();
                    if (val == null) {
                        return Mono.error(new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Sensor state not found for sensorId: " + sensorId
                        ));
                    }
                    return Mono.just(val);
                });
    }
}
