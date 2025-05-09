package com.org.taskorch.restcontroller;

import com.org.taskorch.document.TaskRequest;
import com.org.taskorch.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final KafkaProducerService kafkaProducerService;

    public TaskController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping
    public ResponseEntity<String> handleCommandRequest(@RequestBody TaskRequest commandRequest) {
        try {
            // Log the deserialized CommandRequest object
            logger.debug("Raw JSON payload received: {}", commandRequest);
            logger.debug("Deserialized CommandRequest: {}", commandRequest);

            logger.info("Received Command Request: {}", commandRequest);

            // Process the command request (save to MongoDB and send to Kafka)
            kafkaProducerService.processCommandRequest(commandRequest);

            logger.info("Command successfully processed and sent to Kafka.");
            return new ResponseEntity<>("Command sent to Kafka successfully", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error processing Command Request: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to process Command Request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}