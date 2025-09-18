package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.model.MessagePayload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class RequestConsumer {

    @RabbitListener(queues = "${messaging.rabbitmq.request-queue}")
    public void receiveResponse(MessagePayload payload) {
        System.out.println("✅ Received request from external service:");
        System.out.println("Sensor ID: " + payload.getSensorId());
        System.out.println("Operation: " + payload.getOperation());
        System.out.println("Relay Number: " + payload.getRelayNumber());
        System.out.println("Duration: " + payload.getDuration());
    }
}
