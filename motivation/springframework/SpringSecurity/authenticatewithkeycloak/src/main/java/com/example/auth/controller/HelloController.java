package com.example.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/public")
    public String publicEndpoint() {
        log.info("Handling request for /public");
        return "This is a public endpoint.";
    }

    @GetMapping("/secure")
    public String secureEndpoint() {
        log.info("Handling request for /secure");
        return "Hello, secured world!";
    }
}

