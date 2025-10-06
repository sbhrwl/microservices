package com.apexsphere.dataapiservice.exception;

// We extend RuntimeException so that Spring Boot can handle it easily
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}