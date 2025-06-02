package com.example.ingestion.controller;

import com.example.ingestion.dto.PowerQualityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/powerquality")
public class PowerQualityController {

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final List<String> METERS = Arrays.asList("DLMS001", "DLMS002", "DLMS003", "DLMS004", "DLMS005");
    private static final List<String> PHASES = Arrays.asList("A", "B", "C");

    private static final Map<String, Map<String, String>> OBIS_CODES = Map.of(
            "voltage", Map.of("A", "1.0.32.7.0.255", "B", "1.0.52.7.0.255", "C", "1.0.72.7.0.255"),
            "current", Map.of("A", "1.0.31.7.0.255", "B", "1.0.51.7.0.255", "C", "1.0.71.7.0.255"),
            "active_power", Map.of("all", "1.0.1.7.0.255"),
            "reactive_power", Map.of("all", "1.0.3.7.0.255"),
            "power_factor", Map.of("all", "1.0.13.7.0.255")
    );

    @PostMapping("/generate")
    public String generateAndSendData() {
        Instant start = Instant.now();
        int intervals = 4;
        long minutes = 15;

        Random random = new Random();

        for (int i = 0; i < intervals; i++) {
            Instant timestamp = start.plus(i * minutes, ChronoUnit.MINUTES);

            for (String meter : METERS) {
                for (String phase : PHASES) {
                    sendMessage("voltage", OBIS_CODES.get("voltage").get(phase), meter, phase, getRandom(random, 220, 240), timestamp);
                    sendMessage("current", OBIS_CODES.get("current").get(phase), meter, phase, getRandom(random, 4.5, 6.0), timestamp);
                    sendMessage("active_power", OBIS_CODES.get("active_power").get("all"), meter, phase, getRandom(random, 1000, 1500), timestamp);
                    sendMessage("reactive_power", OBIS_CODES.get("reactive_power").get("all"), meter, phase, getRandom(random, 300, 500), timestamp);
                    sendMessage("power_factor", OBIS_CODES.get("power_factor").get("all"), meter, phase, getRandom(random, 0.9, 1.0), timestamp);
                }
            }
        }

        return "Power quality data sent to ActiveMQ";
    }

    private void sendMessage(String measurement, String obisCode, String meterId, String phase, double value, Instant timestamp) {
        PowerQualityMessage msg = new PowerQualityMessage();
        msg.setMeasurement(measurement);
        msg.setObisCode(obisCode);
        msg.setMeterId(meterId);
        msg.setPhase(phase);
        msg.setValue(value);
        msg.setTimestamp(timestamp);

        jmsTemplate.convertAndSend("power-quality-queue", msg);
    }

    private double getRandom(Random random, double min, double max) {
        return Math.round((random.nextDouble() * (max - min) + min) * 100.0) / 100.0;
    }
}
