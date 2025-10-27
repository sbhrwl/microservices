package com.apexsphere.dataapiservice.dto;

public class ControlRequestDTO {

    private Long id;
    private Integer duration;
    private String operation;
    private Integer relayNumber;
    private String sensorId;
    private String status;

    // --- Constructors ---

    public ControlRequestDTO() {
    }

    public ControlRequestDTO(Long id, Integer duration, String operation, Integer relayNumber, String sensorId, String status) {
        this.id = id;
        this.duration = duration;
        this.operation = operation;
        this.relayNumber = relayNumber;
        this.sensorId = sensorId;
        this.status = status;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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
}