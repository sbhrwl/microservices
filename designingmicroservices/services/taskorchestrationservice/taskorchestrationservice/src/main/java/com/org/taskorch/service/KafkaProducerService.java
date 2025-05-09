package com.org.taskorch.service;

import com.org.taskorch.document.TaskRequest;
import com.org.taskorch.repository.TaskRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, TaskRequest> kafkaTemplate;
    private final TaskRequestRepository commandRequestRepository;

    @Value("${spring.kafka.producer.topic}")
    private String topicName;

    public KafkaProducerService(KafkaTemplate<String, TaskRequest> kafkaTemplate,
                                TaskRequestRepository commandRequestRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.commandRequestRepository = commandRequestRepository;
    }

    public void processCommandRequest(TaskRequest commandRequest) {
        try {
            commandRequestRepository.save(commandRequest);
            logger.info("CommandRequest saved to MongoDB: {}", commandRequest);

            // Log the payload being sent to Kafka
            logger.info("Sending payload to Kafka: {}", commandRequest);

            kafkaTemplate.send(topicName, commandRequest.getcommandId(), commandRequest);
            logger.info("Message sent to Kafka topic: {} with key: {}", topicName, commandRequest.getcommandId());
        } catch (Exception e) {
            logger.error("Error processing CommandRequest: {}", e.getMessage(), e);
            throw e;
        }
    }
}