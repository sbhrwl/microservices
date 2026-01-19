package com.example.sensor.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Pure domain model - no framework dependencies.
 * Enforces business invariants.
 */
public final class Sensor {
    
    private final String sensorId;
    private final String userEmail;
    private String postcode;
    private SensorStatus status;
    private final Instant registeredAt;
    private Instant lastUpdatedAt;

    // Private constructor - use factory method
    private Sensor(String sensorId, String userEmail, String postcode, 
                   SensorStatus status, Instant registeredAt, Instant lastUpdatedAt) {
        this.sensorId = sensorId;
        this.userEmail = userEmail;
        this.postcode = postcode;
        this.status = status;
        this.registeredAt = registeredAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    /**
     * Factory method for registering a new sensor.
     */
    public static Sensor register(String sensorId, String userEmail, String postcode) {
        validateSensorId(sensorId);
        validateUserEmail(userEmail);
        validatePostcode(postcode);

        Instant now = Instant.now();
        return new Sensor(sensorId, userEmail, postcode, SensorStatus.ACTIVE, now, now);
    }

    /**
     * Reconstitute from persistence.
     */
    public static Sensor reconstitute(String sensorId, String userEmail, String postcode,
                                      SensorStatus status, Instant registeredAt, Instant lastUpdatedAt) {
        return new Sensor(sensorId, userEmail, postcode, status, registeredAt, lastUpdatedAt);
    }

    /**
     * Update postcode - enforces business rule: postcode must actually change.
     */
    public void updatePostcode(String newPostcode) {
        validatePostcode(newPostcode);
        
        if (this.postcode.equals(newPostcode)) {
            throw new IllegalArgumentException("New postcode must be different from current postcode");
        }
        
        this.postcode = newPostcode;
        this.lastUpdatedAt = Instant.now();
    }

    /**
     * Update sensor status.
     */
    public void updateStatus(SensorStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
        this.lastUpdatedAt = Instant.now();
    }

    // Validation methods
    private static void validateSensorId(String sensorId) {
        if (sensorId == null || sensorId.isBlank()) {
            throw new IllegalArgumentException("Sensor ID cannot be null or blank");
        }
    }

    private static void validateUserEmail(String userEmail) {
        if (userEmail == null || userEmail.isBlank()) {
            throw new IllegalArgumentException("User email cannot be null or blank");
        }
        // Basic email validation
        if (!userEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private static void validatePostcode(String postcode) {
        if (postcode == null || postcode.isBlank()) {
            throw new IllegalArgumentException("Postcode cannot be null or blank");
        }
    }

    // Getters only - immutable where possible
    public String getSensorId() {
        return sensorId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getPostcode() {
        return postcode;
    }

    public SensorStatus getStatus() {
        return status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sensor sensor = (Sensor) o;
        return Objects.equals(sensorId, sensor.sensorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId);
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "sensorId='" + sensorId + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", postcode='" + postcode + '\'' +
                ", status=" + status +
                ", registeredAt=" + registeredAt +
                ", lastUpdatedAt=" + lastUpdatedAt +
                '}';
    }
}