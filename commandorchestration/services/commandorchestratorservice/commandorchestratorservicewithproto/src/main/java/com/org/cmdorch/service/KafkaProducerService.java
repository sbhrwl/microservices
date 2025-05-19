package com.org.cmdorch.service;

import com.org.cmdorch.protobuf.CommandRequest;
import com.org.cmdorch.protobuf.CommandParams;
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

    public void processCommandRequest(com.org.cmdorch.document.CommandRequest commandRequest) {
        try {
            // Save the CommandRequest to MongoDB
            commandRequestRepository.save(commandRequest);
            logger.info("CommandRequest saved to MongoDB: {}", commandRequest);

            // Convert CommandRequest to Protobuf object
            CommandParams commandParams = CommandParams.newBuilder()
                    .setTouProfileId(commandRequest.getCommandParams().getTouProfileId())
                    .build();

            CommandRequest protoRequest = CommandRequest.newBuilder()
                    .setCommandId(commandRequest.getCommandId())
                    .setRequestedBy(commandRequest.getRequestedBy())
                    .addAllDeviceIds(commandRequest.getDeviceIds())
                    .setCommandType(commandRequest.getCommandType())
                    .setCommandParams(commandParams)
                    .build();

            // Log the Protobuf payload being sent to Kafka
            logger.info("Sending Protobuf payload to Kafka: {}", protoRequest);

            // Send the Protobuf message to Kafka
            kafkaTemplate.send(topicName, protoRequest.getCommandId(), protoRequest);
            logger.info("Protobuf message sent to Kafka topic: {} with key: {}", topicName, protoRequest.getCommandId());
        } catch (Exception e) {
            logger.error("Error processing CommandRequest: {}", e.getMessage(), e);
            throw e;
        }
    }
}