package com.apexsphere.storage_service.actors.model;

import java.time.Instant;

public class SensorState {

    private String sensorId;
    private String status;           // "ON" / "OFF"
    private String lastRequestId;    // request ID of last command
    private Instant lastUpdated;     // timestamp when state was last modified

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastRequestId() {
        return lastRequestId;
    }

    public void setLastRequestId(String lastRequestId) {
        this.lastRequestId = lastRequestId;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
