package com.apexsphere.storage_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "request_change_log")
public class RequestChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Assuming Record has an ID, this links the log entry to the specific record
    private Long recordId; 

    // Description of the change (e.g., "Record created", "Status updated")
    private String changeDescription; 

    private Instant changeTimestamp;

    // Default constructor
    public RequestChangeLog() {
        this.changeTimestamp = Instant.now();
    }

    // Constructor for creating a log entry
    public RequestChangeLog(Long recordId, String changeDescription) {
        this.recordId = recordId;
        this.changeDescription = changeDescription;
        this.changeTimestamp = Instant.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
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