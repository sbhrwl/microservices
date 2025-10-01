package com.apexsphere.flexibility_bridge_service.service;

import com.apexsphere.flexibility_bridge_service.model.MessagePayload;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MessageProducerService {
    
    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    private final RabbitTemplate rabbitTemplate;

    // Inject the Exchange name
    @Value("${messaging.rabbitmq.exchange}")
    private String exchange;

    // Inject the Routing Key for the connector request queue
    @Value("${messaging.rabbitmq.request-outbound-routing-key}")
    private String requestOutboundRoutingKey;

    public MessageProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes the request payload to the Connector service's request queue.
     */
    public void sendRequestToConnector(MessagePayload payload) {
        // Exchange: flexibility-bridge.exchange
        // Routing Key: connector.request
        rabbitTemplate.convertAndSend(exchange, requestOutboundRoutingKey, payload);
        log.info("➡️ Published request to connector queue for Sensor ID: {} using key: {}", 
                 payload.getSensorId(), requestOutboundRoutingKey);
    }
}