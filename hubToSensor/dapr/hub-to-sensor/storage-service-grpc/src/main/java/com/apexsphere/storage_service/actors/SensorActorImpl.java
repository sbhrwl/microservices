package com.apexsphere.storage_service.actors;

import com.apexsphere.storage_service.actors.model.SensorState;
import io.dapr.actors.ActorId;
import io.dapr.actors.runtime.AbstractActor;
import io.dapr.actors.runtime.ActorRuntimeContext;
import io.dapr.actors.runtime.ActorStateManager;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class SensorActorImpl extends AbstractActor implements SensorActor {

    private static final String STATE_KEY = "sensorState";

    public SensorActorImpl(ActorRuntimeContext context, ActorId actorId) {
        super(context, actorId);
    }

    @Override
    public Mono<SensorState> applyCommand(String requestId, String operation) {
        ActorStateManager stateManager = this.getActorStateManager();

        return stateManager.get(STATE_KEY, SensorState.class)
                .defaultIfEmpty(new SensorState())
                .flatMap(state -> {
                    String prevStatus = state.getStatus();
                    String newStatus = operation.equalsIgnoreCase("on") ? "ON" : "OFF";

                    if (prevStatus != null && prevStatus.equalsIgnoreCase(newStatus)) {
                        return Mono.just(state); // no change
                    }

                    state.setStatus(newStatus);
                    state.setLastUpdated(Instant.now());
                    state.setLastRequestId(requestId);

                    return stateManager.set(STATE_KEY, state)
                            .then(Mono.just(state));
                });
    }

    @Override
    public Mono<SensorState> getState() {
        ActorStateManager stateManager = this.getActorStateManager();
        return stateManager.get(STATE_KEY, SensorState.class)
                .defaultIfEmpty(new SensorState());
    }
}
