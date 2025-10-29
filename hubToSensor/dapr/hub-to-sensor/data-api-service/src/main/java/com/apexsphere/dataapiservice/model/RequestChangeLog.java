package com.apexsphere.dataapiservice.model;

import java.time.LocalDateTime;

public class RequestChangeLog {

    private Long id;

    private String changeDescription;

    private LocalDateTime changeTimestamp;

    private Long recordId;

    private ControlRequest controlRequest;

    // --- Constructors ---

    public RequestChangeLog() {
    }

    public RequestChangeLog(Long id, String changeDescription, LocalDateTime changeTimestamp, Long recordId, ControlRequest controlRequest) {
        this.id = id;
        this.changeDescription = changeDescription;
        this.changeTimestamp = changeTimestamp;
        this.recordId = recordId;
        this.controlRequest = controlRequest;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public LocalDateTime getChangeTimestamp() {
        return changeTimestamp;
    }

    public void setChangeTimestamp(LocalDateTime changeTimestamp) {
        this.changeTimestamp = changeTimestamp;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public ControlRequest getControlRequest() {
        return controlRequest;
    }

    public void setControlRequest(ControlRequest controlRequest) {
        this.controlRequest = controlRequest;
    }
}