package com.apexsphere.dataapiservice.dto;

import java.time.LocalDateTime;

public class ChangeLogDTO {

    private Long id;
    private String description;       // maps to DB column 'description'
    private LocalDateTime changeTimestamp;

    // --- Constructors ---

    public ChangeLogDTO() {
    }

    public ChangeLogDTO(Long id, String description, LocalDateTime changeTimestamp) {
        this.id = id;
        this.description = description;
        this.changeTimestamp = changeTimestamp;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
