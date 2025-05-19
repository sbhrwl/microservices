package com.example.apikeyauth;

import com.example.apikeyauth.config.ApiKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApiKeyProperties.class)
public class ApikeysApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApikeysApplication.class, args);
	}
}