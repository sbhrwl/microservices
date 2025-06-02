package com.example.ingestion.service;

import com.example.ingestion.dto.PowerQualityMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PowerQualityConsumer {

    private final InfluxDBService influxDBService;

    public PowerQualityConsumer(InfluxDBService influxDBService) {
        this.influxDBService = influxDBService;
    }

    @JmsListener(destination = "power-quality-queue")
    public void receiveMessage(PowerQualityMessage message) {
        System.out.println("Received message: " + message);

        // Wrap into a list for your influx service
        influxDBService.sendToInflux(List.of(message));
    }
}