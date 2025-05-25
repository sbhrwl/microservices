package com.example.apikeyauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/public")
    public String publicHello() {
        return "Hello from public endpoint";
    }

    @GetMapping("/secure")
    public String secureHello() {
        return "Hello from secured API key endpoint";
    }
}
