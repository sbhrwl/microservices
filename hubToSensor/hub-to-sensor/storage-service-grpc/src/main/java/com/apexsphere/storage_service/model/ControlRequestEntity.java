package com.apexsphere.storage_service.postgres.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "control_requests")
public class ControlRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sensorId;
    private String operation;
    private Integer relayNumber;
    private Integer duration;
    private String status;

    private Instant createdAt;
    private Instant updatedAt;

    @OneToMany(mappedBy = "controlRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequestChangeLogEntity> changeLogs = new ArrayList<>();

    public ControlRequestEntity() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Constructor from gRPC request
    public ControlRequestEntity(String sensorId, String operation, Integer relayNumber, Integer duration, String status) {
        this.sensorId = sensorId;
        this.operation = operation;
        this.relayNumber = relayNumber;
        this.duration = duration;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Update entity from request (all fields)
    public void updateFromRequest(String operation, Integer relayNumber, Integer duration, String status) {
        this.operation = operation;
        this.relayNumber = relayNumber;
        this.duration = duration;
        this.status = status;
        this.updatedAt = Instant.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
        this.updatedAt = Instant.now();
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
        this.updatedAt = Instant.now();
    }

    public Integer getRelayNumber() {
        return relayNumber;
    }

    public void setRelayNumber(Integer relayNumber) {
        this.relayNumber = relayNumber;
        this.updatedAt = Instant.now();
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
        this.updatedAt = Instant.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<RequestChangeLogEntity> getChangeLogs() {
        return changeLogs;
    }

    public void addChangeLog(RequestChangeLogEntity log) {
        log.setControlRequest(this);
        this.changeLogs.add(log);
    }
}
