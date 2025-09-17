package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.config.RabbitMQConfig;
import com.apexsphere.flexibility_hub_simulator.model.MessagePayload;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQConfig config;

    public MessagePublisher(RabbitTemplate rabbitTemplate, RabbitMQConfig config) {
        this.rabbitTemplate = rabbitTemplate;
        this.config = config;
    }

    /**
     * Publish a message to the request queue using the request routing key.
     */
    public void publishToRequestQueue(MessagePayload payload) {
        rabbitTemplate.convertAndSend(
            config.getExchangeName(),
            config.getRequestRoutingKey(), // ✅ use request routing key
            payload
        );
    }

    /**
     * (Optional) Publish a message to the response queue using the response routing key.
     * Useful if the simulator needs to emit responses as well.
     */
    public void publishToResponseQueue(MessagePayload payload) {
        rabbitTemplate.convertAndSend(
            config.getExchangeName(),
            config.getResponseRoutingKey(), // ✅ use response routing key
            payload
        );
    }

    /**
     * (Optional) If you want to rely on the RabbitTemplate defaults set in RabbitMQConfig:
     * template.setExchange(exchangeName);
     * template.setRoutingKey(requestRoutingKey);
     * then you could simply call:
     *
     * rabbitTemplate.convertAndSend(payload);
     *
     * which will go to the request queue by default.
     */
}
