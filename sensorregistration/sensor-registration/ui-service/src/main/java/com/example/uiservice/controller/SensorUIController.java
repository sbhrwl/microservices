package com.example.uiservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class SensorUIController {

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
        model.addAttribute("keycloakUrl", keycloakUrl);
        model.addAttribute("keycloakRealm", keycloakRealm);
        model.addAttribute("keycloakClientId", keycloakClientId);
        model.addAttribute("sensorServiceUrl", sensorServiceUrl);
        return "sensor-form";
    }
}