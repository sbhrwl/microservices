// src/main/java/com/example/ingestionservice/IngestionServiceApplication.java
package com.example.ingestionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Import for @Bean
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@SpringBootApplication
@EnableJms // Enable JMS functionality for ActiveMQ message listening
// Add ComponentScan to ensure Spring finds the new controller in its package
@ComponentScan(basePackages = {"com.example.ingestionservice.controller", "com.example.ingestionservice.listener"})
public class IngestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngestionServiceApplication.class, args);
    }

    /**
     * Configures a MessageConverter to enable automatic JSON serialization/deserialization
     * for JMS messages. This allows JmsTemplate to send POJOs as JSON strings
     * and JmsListener to receive JSON strings as POJOs.
     * @return The configured MessageConverter.
     */
    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        // Set the message type to TEXT so the payload is sent as a String (JSON)
        converter.setTargetType(MessageType.TEXT);
        // Set a type ID property. This helps the receiver deserialize the message correctly
        // if they also use a MappingJackson2MessageConverter.
        converter.setTypeIdPropertyName("_type");
        return converter;
    }
}
