package com.apexsphere.protocol_adapter_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.WebApplicationType;

@SpringBootApplication
public class ProtocolAdapterApplication {

    public static void main(String[] args) {
        // Start as a web application to expose Dapr subscription endpoints
        SpringApplication application = new SpringApplication(ProtocolAdapterApplication.class);
        application.setWebApplicationType(WebApplicationType.SERVLET);
        application.run(args);
    }
}
