package com.example.ingestion.dto;

import java.time.Instant;

public class PowerQualityMessage {
    private String measurement;
    private String obisCode;
    private String meterId;
    private String phase;
    private double value;
    private Instant timestamp;

    public String getMeasurement() {
        return measurement;
    }
    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }
    public String getObisCode() {
        return obisCode;
    }
    public void setObisCode(String obisCode) {
        this.obisCode = obisCode;
    }
    public String getMeterId() {
        return meterId;
    }
    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }
    public String getPhase() {
        return phase;
    }
    public void setPhase(String phase) {
        this.phase = phase;
    }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
