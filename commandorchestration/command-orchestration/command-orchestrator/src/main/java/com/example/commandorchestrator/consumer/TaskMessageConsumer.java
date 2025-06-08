package com.example.commandorchestrator.consumer;

import com.example.taskservice.TaskMessageProto; // Import the generated Protobuf class
import com.example.commandorchestrator.service.CommandOrchestrationService; // Import the new service
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaskMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TaskMessageConsumer.class);

    private final CommandOrchestrationService commandOrchestrationService; // Inject the service

    // Constructor for dependency injection
    public TaskMessageConsumer(CommandOrchestrationService commandOrchestrationService) {
        this.commandOrchestrationService = commandOrchestrationService;
    }

    @KafkaListener(topics = "${kafka.topic.task-registration}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, byte[]> record) {
        String taskId = record.key();
        byte[] messageBytes = record.value();

        logger.info("Received Kafka message with key: {} from topic: {}", taskId, record.topic());

        try {
            TaskMessageProto.TaskMessage taskMessage = TaskMessageProto.TaskMessage.parseFrom(messageBytes);
            logger.info("Successfully deserialized TaskMessage: {}", taskMessage.toString());

            // Pass the deserialized message to the orchestration service
            commandOrchestrationService.orchestrateTaskCommands(taskMessage);

        } catch (IOException e) {
            logger.error("Failed to deserialize Protobuf message for key: {}", taskId, e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while processing message for key: {}", taskId, e);
        }
    }
}