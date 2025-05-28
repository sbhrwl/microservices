package com.example.uiservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class SensorUIController {

    private static final Logger logger = LoggerFactory.getLogger(SensorUIController.class);

    @Value("${keycloak.url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.clientId}")
    private String keycloakClientId;

    @Value("${sensor.service.url}")
    private String sensorServiceUrl;

    @GetMapping("/")
    public String redirectToSensorForm() {
        return "redirect:/sensor-form";
    }

    @GetMapping("/sensor-form")
    public String showSensorForm(Model model) {
        logger.info("Keycloak URL: {}", keycloakUrl);
        logger.info("Keycloak Realm: {}", keycloakRealm);
        logger.info("Keycloak Client ID: {}", keycloakClientId);

        model.addAttribute("keycloakUrl", keycloakUrl);
        model.addAttribute("keycloakRealm", keycloakRealm);
        model.addAttribute("keycloakClientId", keycloakClientId);
        model.addAttribute("sensorServiceUrl", sensorServiceUrl);
        return "sensor-form";
    }
}