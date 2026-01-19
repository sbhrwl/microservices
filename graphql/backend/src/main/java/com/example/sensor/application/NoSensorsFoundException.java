package com.example.sensor.application;

public class NoSensorsFoundException extends RuntimeException {
    public NoSensorsFoundException(String message) {
        super(message);
    }
}