package com.tdf.activeamqp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ActiveAmqpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActiveAmqpApplication.class, args);
    }
}
// POST http://localhost:2012/v1/publish?message=test