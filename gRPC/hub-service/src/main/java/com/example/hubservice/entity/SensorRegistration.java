package com.example.hubservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * SensorRegistration is a JPA Entity that maps to the 'sensor_registrations' table in the database.
 * It represents the data for a registered sensor, including its ID, model, email, and registration timestamp.
 */
@Entity
@Table(name = "sensor_registrations") // Maps this entity to the 'sensor_registrations' table
public class SensorRegistration {

    @Id // Specifies the primary key of the entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the ID using database identity column
    private Long id;

    @Column(name = "sensor_id", unique = true, nullable = false) // Maps to 'sensor_id' column, must be unique and not null
    private String sensorId;

    @Column(name = "sensor_model", nullable = false) // Maps to 'sensor_model' column, not null
    private String sensorModel;

    @Column(name = "email", nullable = false) // Maps to 'email' column, not null
    private String email;

    @Column(name = "registration_timestamp", nullable = false) // Maps to 'registration_timestamp' column, not null
    private LocalDateTime registrationTimestamp; // Stores the timestamp of registration

    // Default constructor (required by JPA)
    public SensorRegistration() {
    }

    // Constructor for creating new instances
    public SensorRegistration(String sensorId, String sensorModel, String email, LocalDateTime registrationTimestamp) {
        this.sensorId = sensorId;
        this.sensorModel = sensorModel;
        this.email = email;
        this.registrationTimestamp = registrationTimestamp;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getRegistrationTimestamp() {
        return registrationTimestamp;
    }

    public void setRegistrationTimestamp(LocalDateTime registrationTimestamp) {
        this.registrationTimestamp = registrationTimestamp;
    }

    @Override
    public String toString() {
        return "SensorRegistration{" +
               "id=" + id +
               ", sensorId='" + sensorId + '\'' +
               ", sensorModel='" + sensorModel + '\'' +
               ", email='" + email + '\'' +
               ", registrationTimestamp=" + registrationTimestamp +
               '}';
    }
}
