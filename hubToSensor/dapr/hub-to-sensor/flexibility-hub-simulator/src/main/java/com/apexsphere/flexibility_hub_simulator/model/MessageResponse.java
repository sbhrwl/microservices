package com.apexsphere.flexibility_hub_simulator.model;

import java.io.Serializable;

/**
 * Model representing the final response received by the Hub Simulator from the Bridge.
 * This structure mirrors the FlexibilityResponse sent upstream.
 */
public class MessageResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;
    private String status;
    private String message;
    private String errorCode; 
    private String timestamp;

    // Default constructor for Jackson
    public MessageResponse() {
    }

    // Getters and Setters

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "requestId='" + requestId + '\'' +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
