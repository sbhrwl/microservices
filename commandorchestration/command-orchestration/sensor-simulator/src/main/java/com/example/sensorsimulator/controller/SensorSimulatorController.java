package com.example.sensorsimulator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("${sensor.simulator.endpoint:/simulate}")
public class SensorSimulatorController {

    private static final Logger logger = LoggerFactory.getLogger(SensorSimulatorController.class);

    @PostMapping
    public ResponseEntity<String> simulateSensor(HttpServletRequest request) {
        try {
            byte[] payload = request.getInputStream().readAllBytes();
            String payloadStr = new String(payload, StandardCharsets.UTF_8);

            logger.info("Received sensor simulation request with payload: {}", payloadStr);

            // You could parse sensorId from payloadStr if needed here

            // Simulate sensor response or just acknowledge
            return ResponseEntity.ok("Simulated sensor received payload of length " + payload.length);

        } catch (IOException e) {
            logger.error("Failed to read request payload", e);
            return ResponseEntity.status(500).body("Error reading request payload");
        }
    }
}
