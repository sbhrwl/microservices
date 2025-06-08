package com.example.protocolgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ProtocolGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProtocolGatewayApplication.class, args);
    }
}
