package com.apexsphere.dataapiservice.dto;

import java.time.LocalDateTime;

public class ChangeLogDTO {

    private Long id;
    private String changeDescription;
    private LocalDateTime changeTimestamp;

    // We exclude the 'recordId' foreign key for API consumer cleanliness.

    // --- Constructors ---

    public ChangeLogDTO() {
    }

    public ChangeLogDTO(Long id, String changeDescription, LocalDateTime changeTimestamp) {
        this.id = id;
        this.changeDescription = changeDescription;
        this.changeTimestamp = changeTimestamp;
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
}