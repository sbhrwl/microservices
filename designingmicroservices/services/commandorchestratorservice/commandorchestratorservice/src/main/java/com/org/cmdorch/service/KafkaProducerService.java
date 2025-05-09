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

    /**
     * Processes a CommandRequest by saving it to MongoDB and sending it to Kafka.
     *
     * @param commandRequest the CommandRequest to process
     */
    public void processCommandRequest(CommandRequest commandRequest) {
        try {
            // Save to MongoDB
            commandRequestRepository.save(commandRequest);
            logger.info("CommandRequest saved to MongoDB: {}", commandRequest);

            // Send to Kafka
            kafkaTemplate.send(topicName, commandRequest.getCorrelationId(), commandRequest);
            logger.info("Message sent to Kafka topic: {} with key: {}", topicName, commandRequest.getCorrelationId());
        } catch (Exception e) {
            logger.error("Error processing CommandRequest: {}", e.getMessage(), e);
            throw e; // Re-throw the exception for higher-level handling
        }
    }
}