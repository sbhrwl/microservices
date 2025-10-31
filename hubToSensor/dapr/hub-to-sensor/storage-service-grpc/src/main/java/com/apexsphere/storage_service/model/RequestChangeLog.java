package com.apexsphere.storage_service.model;

// REMOVE java.time.Instant import
// import java.time.Instant; 
import java.io.Serializable; // Added for completeness, though often not strictly required for Dapr POJOs

public class RequestChangeLog implements Serializable {

    private String id;
    private String recordId;
    private String changeDescription;
    // CHANGE: Use long for epoch timestamp instead of Instant
    private long changeTimestamp; 

    public RequestChangeLog() {
        this.changeTimestamp = System.currentTimeMillis();
    }

    public RequestChangeLog(String recordId, String changeDescription) {
        this.recordId = recordId;
        this.changeDescription = changeDescription;
        this.changeTimestamp = System.currentTimeMillis();
    }
    
    // Omitted the 3-arg constructor for brevity, assuming you won't use it or will update it similarly.

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

    // UPDATED Getter and Setter signature
    public long getChangeTimestamp() {
        return changeTimestamp;
    }

    // UPDATED Setter signature
    public void setChangeTimestamp(long changeTimestamp) {
        this.changeTimestamp = changeTimestamp;
    }
}