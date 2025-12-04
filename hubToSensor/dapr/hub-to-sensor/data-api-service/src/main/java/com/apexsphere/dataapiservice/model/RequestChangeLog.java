package com.apexsphere.dataapiservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_change_log")
public class RequestChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Maps to "control_request_id"
    @Column(name = "control_request_id", nullable = false)
    private Long controlRequestId;

    // Maps to "description"
    @Column(name = "description", nullable = false)
    private String description;

    // Maps to "change_timestamp"
    @Column(name = "change_timestamp", nullable = false)
    private LocalDateTime changeTimestamp;

    // Relationship back to ControlRequest
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "control_request_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ControlRequest controlRequest;

    // --- Constructors ---
    public RequestChangeLog() {
    }

    public RequestChangeLog(Long id, Long controlRequestId, String description, LocalDateTime changeTimestamp) {
        this.id = id;
        this.controlRequestId = controlRequestId;
        this.description = description;
        this.changeTimestamp = changeTimestamp;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public Long getControlRequestId() {
        return controlRequestId;
    }

    public void setControlRequestId(Long controlRequestId) {
        this.controlRequestId = controlRequestId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getChangeTimestamp() {
        return changeTimestamp;
    }

    public void setChangeTimestamp(LocalDateTime changeTimestamp) {
        this.changeTimestamp = changeTimestamp;
    }

    public ControlRequest getControlRequest() {
        return controlRequest;
    }

    public void setControlRequest(ControlRequest controlRequest) {
        this.controlRequest = controlRequest;
    }
}
