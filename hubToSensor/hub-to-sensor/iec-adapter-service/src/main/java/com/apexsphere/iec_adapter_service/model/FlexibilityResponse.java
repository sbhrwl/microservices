package com.apexsphere.iec_adapter_service.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

@XmlRootElement(name = "FlexibilityResponse")
public class FlexibilityResponse {

    private String requestId;
    private String status;
    private String message;
    private String errorCode; // Only present on ERROR
    private String timestamp;

    // Add default constructor for JAXB
    public FlexibilityResponse() {
    }

    // --- requestId ---
    @XmlElement(name = "RequestID")
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    // --- status ---
    @XmlElement(name = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    // --- message ---
    @XmlElement(name = "Message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // --- errorCode (Optional field) ---
    @XmlElement(name = "ErrorCode")
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    // --- timestamp ---
    @XmlElement(name = "Timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    // --- toString() for logging/debugging ---
    @Override
    public String toString() {
        return "FlexibilityResponse{" +
               "requestId='" + requestId + '\'' +
               ", status='" + status + '\'' +
               ", message='" + message + '\'' +
               ", errorCode='" + errorCode + '\'' +
               ", timestamp='" + timestamp + '\'' +
               '}';
    }
}