package com.apexsphere.dataapiservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "request_change_log")
public class RequestChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Maps to 'id'

    @Column(name = "change_description")
    private String changeDescription; // Maps to 'change_description'

    @Column(name = "change_timestamp")
    private LocalDateTime changeTimestamp; // Maps to 'change_timestamp'

    @Column(name = "record_id")
    private Long recordId; // Maps to 'record_id' (The Foreign Key value)

    // JPA Mapping: Many RequestChangeLogs belong to One ControlRequest
    // This field establishes the actual link using the 'record_id' column.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", referencedColumnName = "id", insertable = false, updatable = false)
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