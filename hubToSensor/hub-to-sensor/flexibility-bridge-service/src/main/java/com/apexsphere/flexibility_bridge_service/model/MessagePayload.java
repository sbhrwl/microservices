package com.apexsphere.flexibility_bridge_service.model;

import java.io.Serializable;

public class MessagePayload implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sensorId;
    private String operation;
    private int relayNumber;
    private int duration;
    
    // --- FIX: ADDED FIELD FOR TRACEABILITY ---
    private String recordId;


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
    
    // --- FIX: ADDED GETTER AND SETTER FOR recordId ---
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
    
    // --- ADDED for debugging and logging ---
    @Override
    public String toString() {
        return "MessagePayload{" +
                "recordId='" + recordId + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", operation='" + operation + '\'' +
                ", relayNumber=" + relayNumber +
                ", duration=" + duration +
                '}';
    }
}