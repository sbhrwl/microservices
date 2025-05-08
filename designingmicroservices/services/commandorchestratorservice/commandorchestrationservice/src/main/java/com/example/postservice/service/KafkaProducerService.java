package com.example.postservice.service;

import com.example.postservice.model.CommandRequest;
import com.example.postservice.repository.CommandRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, CommandRequest> kafkaTemplate;
    private final CommandRequestRepository commandRequestRepository;

    @Value("${spring.kafka.producer.topic}")
    private String topicName;

    public KafkaProducerService(KafkaTemplate<String, CommandRequest> kafkaTemplate,
                                CommandRequestRepository commandRequestRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.commandRequestRepository = commandRequestRepository;
    }

    public void sendCommandRequest(CommandRequest commandRequest) {
        // Save to MongoDB
        commandRequestRepository.save(commandRequest);
        System.out.println("CommandRequest saved to MongoDB: " + commandRequest);

        // Send to Kafka
        kafkaTemplate.send(topicName, commandRequest.getCorrelationId(), commandRequest);
        System.out.println("Message sent to Kafka topic: " + topicName);
    }
}