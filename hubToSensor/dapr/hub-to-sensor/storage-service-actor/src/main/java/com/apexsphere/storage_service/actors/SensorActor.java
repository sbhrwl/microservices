package com.apexsphere.storage_service.actors;

import com.apexsphere.storage_service.actors.model.SensorState;
import reactor.core.publisher.Mono;

public interface SensorActor {

    // Command to switch ON or OFF
    Mono<SensorState> applyCommand(String requestId, String operation);

    // Query the current state
    Mono<SensorState> getState();
}
