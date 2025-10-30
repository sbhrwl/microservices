package com.apexsphere.storage_service.model;

import java.time.Instant;

public class RequestChangeLog {

    private String id;              // Dapr will store this as the state key
    private String recordId;        // Links to the related record
    private String changeDescription;
    private Instant changeTimestamp;

    public RequestChangeLog() {
        this.changeTimestamp = Instant.now();
    }

    public RequestChangeLog(String recordId, String changeDescription) {
        this.recordId = recordId;
        this.changeDescription = changeDescription;
        this.changeTimestamp = Instant.now();
    }

    public RequestChangeLog(String id, String recordId, String changeDescription) {
        this.id = id;
        this.recordId = recordId;
        this.changeDescription = changeDescription;
        this.changeTimestamp = Instant.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public Instant getChangeTimestamp() {
        return changeTimestamp;
    }

    public void setChangeTimestamp(Instant changeTimestamp) {
        this.changeTimestamp = changeTimestamp;
    }
}
