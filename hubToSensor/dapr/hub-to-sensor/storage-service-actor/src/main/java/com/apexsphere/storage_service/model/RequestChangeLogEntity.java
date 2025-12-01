package com.apexsphere.storage_service.postgres.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "request_change_log")
public class RequestChangeLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Instant changeTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "control_request_id", nullable = false)
    private ControlRequestEntity controlRequest;

    public RequestChangeLogEntity() {
        this.changeTimestamp = Instant.now();
    }

    public RequestChangeLogEntity(String description) {
        this.description = description;
        this.changeTimestamp = Instant.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Instant getChangeTimestamp() {
        return changeTimestamp;
    }

    public ControlRequestEntity getControlRequest() {
        return controlRequest;
    }

    public void setControlRequest(ControlRequestEntity controlRequest) {
        this.controlRequest = controlRequest;
    }
}
