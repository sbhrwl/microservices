package com.apexsphere.flexibility_bridge_service.model;

import java.io.Serializable;

public class MessagePayload implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sensorId;
    private String operation;
    private int relayNumber;
    private int duration;


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

    public int getRelayNumber() {
        return relayNumber;
    }

    public void setRelayNumber(int relayNumber) {
        this.relayNumber = relayNumber;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
