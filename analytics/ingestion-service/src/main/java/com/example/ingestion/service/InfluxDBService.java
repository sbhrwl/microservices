package com.example.ingestion.service;

import com.example.ingestion.config.InfluxDBProperties;
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
    private final InfluxDBProperties influxDBProperties;

    public InfluxDBService(InfluxDBProperties influxDBProperties) {
        this.influxDBProperties = influxDBProperties;
    }

    public void sendToInflux(List<PowerQualityMessage> messages) {
        String influxUrl = buildInfluxUrl();

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

    private String buildInfluxUrl() {
        return String.format("%s/write?db=%s&u=%s&p=%s",
                influxDBProperties.getHost(),
                influxDBProperties.getDatabase(),
                influxDBProperties.getUsername(),
                influxDBProperties.getPassword());
    }

    private String convertToLineProtocol(PowerQualityMessage msg) {
        long tsNs = msg.getTimestamp().toEpochMilli() * 1_000_000L;

        String measurementName = influxDBProperties.isSingleMeasurement()
                ? "power_quality"
                : msg.getMeasurement(); // like "voltage", "current"

        String fieldKey = influxDBProperties.isSingleMeasurement()
                ? msg.getMeasurement() // field key: voltage=..., current=...
                : "value";             // default field key

        return String.format("%s,obis_code=%s,phase=%s,meter_id=%s %s=%f %d",
                measurementName,
                msg.getObisCode(),
                msg.getPhase(),
                msg.getMeterId(),
                fieldKey,
                msg.getValue(),
                tsNs);
    }
}
