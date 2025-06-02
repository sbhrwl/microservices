package com.example.ingestion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "custom.influxdb")
public class InfluxDBProperties {
    private String host;
    private String database;
    private String username;
    private String password;
    private boolean singleMeasurement;

    // Getters and Setters
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isSingleMeasurement() { return singleMeasurement; }
    public void setSingleMeasurement(boolean singleMeasurement) { this.singleMeasurement = singleMeasurement; }
}
