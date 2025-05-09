package com.org.cmdorch.restcontroller;

import com.org.cmdorch.document.CommandRequest;
import com.org.cmdorch.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commands")
public class CommandController {

    private static final Logger logger = LoggerFactory.getLogger(CommandController.class);

    private final KafkaProducerService kafkaProducerService;

    public CommandController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping
    public ResponseEntity<String> handleCommandRequest(@RequestBody CommandRequest commandRequest) {
        logger.info("Received Command Request: {}", commandRequest);

        // Send the command request to Kafka
        kafkaProducerService.sendCommandRequest(commandRequest);

        logger.info("Command successfully processed and sent to Kafka.");
        return new ResponseEntity<>("Command sent to Kafka successfully", HttpStatus.OK);
    }
}