package com.example.ingestionservice.model;

// This POJO (Plain Old Java Object) maps directly to the incoming JSON structure
public class RegistrationRequestPojo {
    private String sensorId;
    private String sensorModel;
    private String email;

    // Getters and Setters
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

    @Override
    public String toString() {
        return "RegistrationRequestPojo{" +
               "sensorId='" + sensorId + '\'' +
               ", sensorModel='" + sensorModel + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}