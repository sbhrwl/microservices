package com.example.ingestion.service;

import com.example.ingestion.dto.PowerQualityMessage;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfluxDBService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String influxUrl = "http://localhost:8086/write?db=power_quality&u=admin&p=admin123";

    public void sendToInflux(List<PowerQualityMessage> messages) {
        // Convert PowerQualityMessage objects to InfluxDB line protocol format
        List<String> lines = messages.stream()
                .map(this::convertToLineProtocol)
                .collect(Collectors.toList());

        String data = String.join("\n", lines);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        HttpEntity<String> request = new HttpEntity<>(data, headers);

        try {
            restTemplate.postForEntity(influxUrl, request, String.class);
            System.out.println("Successfully pushed " + messages.size() + " points to InfluxDB");
        } catch (Exception e) {
            System.err.println("Error sending data to InfluxDB: " + e.getMessage());
        }
    }

    private String convertToLineProtocol(PowerQualityMessage msg) {
        // Format timestamp in nanoseconds since epoch
        long tsNs = msg.getTimestamp().toEpochMilli() * 1_000_000L;

        // Example line protocol format:
        // measurement,obis_code=...,phase=...,meter_id=... value=... timestamp
        return String.format("%s,obis_code=%s,phase=%s,meter_id=%s value=%f %d",
                msg.getMeasurement(),
                msg.getObisCode(),
                msg.getPhase(),
                msg.getMeterId(),
                msg.getValue(),
                tsNs);
    }
}
