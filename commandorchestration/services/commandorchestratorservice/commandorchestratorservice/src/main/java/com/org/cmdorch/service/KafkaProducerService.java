package com.org.cmdorch.service;

import com.org.cmdorch.document.CommandRequest;
import com.org.cmdorch.repository.CommandRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, CommandRequest> kafkaTemplate;
    private final CommandRequestRepository commandRequestRepository;

    @Value("${spring.kafka.producer.topic}")
    private String topicName;

    public KafkaProducerService(KafkaTemplate<String, CommandRequest> kafkaTemplate,
                                CommandRequestRepository commandRequestRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.commandRequestRepository = commandRequestRepository;
    }

    public void processCommandRequest(CommandRequest commandRequest) {
        try {
            commandRequestRepository.save(commandRequest);
            logger.info("CommandRequest saved to MongoDB: {}", commandRequest);

            // Log the payload being sent to Kafka
            logger.info("Sending payload to Kafka: {}", commandRequest);

            kafkaTemplate.send(topicName, commandRequest.getCommandId(), commandRequest);
            logger.info("Message sent to Kafka topic: {} with key: {}", topicName, commandRequest.getCommandId());
        } catch (Exception e) {
            logger.error("Error processing CommandRequest: {}", e.getMessage(), e);
            throw e;
        }
    }
}