package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import jakarta.jms.ConnectionFactory;

@Configuration
@EnableJms
public class JmsConfig {


    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory jmsListenerContainerFactory = new DefaultJmsListenerContainerFactory();

        jmsListenerContainerFactory.setConnectionFactory(connectionFactory);
        // jmsListenerContainerFactory.setConcurrency("5-10");
        jmsListenerContainerFactory.setConcurrency("1-1");
        return jmsListenerContainerFactory;
    }
}
