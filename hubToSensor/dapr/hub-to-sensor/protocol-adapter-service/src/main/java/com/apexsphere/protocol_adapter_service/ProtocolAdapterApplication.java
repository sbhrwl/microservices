package com.apexsphere.protocol_adapter_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
public class ProtocolAdapterApplication {

    public static void main(String[] args) {
        // Explicitly set the application type to NONE (non-web) to prevent starting an embedded web server (e.g., on port 8080).
        // This application only needs to listen to RabbitMQ and communicate via gRPC.
        SpringApplication application = new SpringApplication(ProtocolAdapterApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.run(args);
    }
}
