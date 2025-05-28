package com.example.uiservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component; // Added import

import org.slf4j.Logger; // Added import
import org.slf4j.LoggerFactory; // Added import

@SpringBootApplication
public class UiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UiServiceApplication.class, args);
    }

}

// Add this new component class
@Component
class KeycloakUrlVerifier implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(KeycloakUrlVerifier.class);

    @Value("${keycloak.url}") // Inject the property from application.properties or env var
    private String actualKeycloakUrl;

    @Override
    public void run(String... args) throws Exception {
        log.info("DEBUG: Keycloak URL configured in application: {}", actualKeycloakUrl);
    }
}