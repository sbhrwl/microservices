package com.example.sensor.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.sensor.domain.Sensor;
import com.example.sensor.domain.SensorStatus;

import java.time.Instant;

@Document(collection = "sensors")
public class SensorDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String sensorId;

    private String sensorModel;
    private String userEmail;
    private String postcode;
    private String status;

    private Instant createdAt;
    private Instant updatedAt;

    // Default constructor
    public SensorDocument() {
    }

    // Constructor from domain object
    public SensorDocument(Sensor sensor) {
        this.sensorId = sensor.getSensorId();
        this.userEmail = sensor.getUserEmail();
        this.postcode = sensor.getPostcode();
        this.status = sensor.getStatus().name();
        this.createdAt = sensor.getRegisteredAt();
        this.updatedAt = sensor.getLastUpdatedAt();
    }

    // Convert to domain object using reconstitute factory method
    public Sensor toDomain() {
        return Sensor.reconstitute(
            this.sensorId,
            this.userEmail,
            this.postcode,
            SensorStatus.valueOf(this.status),
            this.createdAt,
            this.updatedAt
        );
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorModel() {
        return sensorModel;
    }

    public void setSensorModel(String sensorModel) {
        this.sensorModel = sensorModel;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
