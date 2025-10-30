package com.apexsphere.storage_service.model;

import java.io.Serializable;

/**
 * Represents a control request stored in Dapr state store.
 * Converted from JPA entity to plain POJO for Dapr-based persistence.
 */
public class Record implements Serializable {

    private String id;
    private String sensorId;
    private String operation;
    private Integer relayNumber;
    private Integer duration;
    private String status;

    public Record() {
    }

    public Record(String id, String sensorId, String operation, Integer relayNumber, Integer duration, String status) {
        this.id = id;
        this.sensorId = sensorId;
        this.operation = operation;
        this.relayNumber = relayNumber;
        this.duration = duration;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Integer getRelayNumber() {
        return relayNumber;
    }

    public void setRelayNumber(Integer relayNumber) {
        this.relayNumber = relayNumber;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
