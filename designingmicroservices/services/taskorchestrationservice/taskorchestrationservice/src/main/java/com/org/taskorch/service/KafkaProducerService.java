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
    private final TaskRequestRepository taskRequestRepository;

    @Value("${spring.kafka.producer.topic}")
    private String topicName;

    public KafkaProducerService(KafkaTemplate<String, TaskRequest> kafkaTemplate,
                                TaskRequestRepository taskRequestRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.taskRequestRepository = taskRequestRepository;
    }

    public void processTaskRequest(TaskRequest taskRequest) {
        try {
            taskRequestRepository.save(taskRequest);
            logger.info("TaskRequest saved to MongoDB: {}", taskRequest);

            // Log the payload being sent to Kafka
            logger.info("Sending payload to Kafka: {}", taskRequest);

            kafkaTemplate.send(topicName, taskRequest.getTaskId(), taskRequest);
            logger.info("Message sent to Kafka topic: {} with key: {}", topicName, taskRequest.getTaskId());
        } catch (Exception e) {
            logger.error("Error processing TaskRequest: {}", e.getMessage(), e);
            throw e;
        }
    }
}