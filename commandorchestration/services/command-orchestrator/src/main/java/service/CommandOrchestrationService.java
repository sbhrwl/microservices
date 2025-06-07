package com.example.commandorchestrator.service;

import com.example.commandorchestrator.model.Command;
import com.example.commandorchestrator.CommandMessageProto; // Your protobuf import
import com.example.taskservice.TaskMessageProto; // Incoming protobuf message
import com.example.commandorchestrator.repository.CommandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CommandOrchestrationService {

    private static final Logger logger = LoggerFactory.getLogger(CommandOrchestrationService.class);

    private final CommandRepository commandRepository;
    private final KafkaProducer kafkaProducer;

    @Value("${command.persistence.enabled:true}")
    private boolean commandPersistenceEnabled;

    public CommandOrchestrationService(CommandRepository commandRepository, KafkaProducer kafkaProducer) {
        this.commandRepository = commandRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public void orchestrateTaskCommands(TaskMessageProto.TaskMessage taskMessage) {
        String taskId = taskMessage.getTaskId();
        var sensorList = taskMessage.getSensorListList();

        // Use protobuf enum type directly here (not String)
        TaskMessageProto.CommandType commandType = taskMessage.getCommandType();
        var commandArgs = taskMessage.getCommandArgsList();

        logger.info("Orchestrating commands for Task ID: {} with {} sensors.", taskId, sensorList.size());

        for (String sensorId : sensorList) {
            // Pass commandType as String to your Command constructor if it expects String
            Command command = new Command(taskId, sensorId, commandType.name(), commandArgs);
            logger.debug("Generated command for sensor {}: {}", sensorId, command.toString());

            if (commandPersistenceEnabled) {
                Command savedCommand = commandRepository.save(command);
                logger.info("Saved command to MongoDB for sensor {} with ID: {}", sensorId, savedCommand.getId());

                // Build protobuf CommandMessage with enum type directly from taskMessage
                CommandMessageProto.CommandMessage protoCommand = CommandMessageProto.CommandMessage.newBuilder()
                        .setId(savedCommand.getId())
                        .setTaskId(savedCommand.getTaskId())
                        .setSensorId(savedCommand.getSensorId())
                        // Convert the String commandType back to enum when building protobuf message
                        .setCommandType(TaskMessageProto.CommandType.valueOf(savedCommand.getCommandType()))
                        .addAllCommandArgs(savedCommand.getCommandArgs())
                        .build();

                kafkaProducer.sendMessage(sensorId, protoCommand.toByteArray());

            } else {
                logger.info("Command persistence is disabled. Skipping saving command for sensor {} to MongoDB.", sensorId);
            }

            logger.info("Command for sensor {} is ready for execution.", sensorId);
        }

        logger.info("Finished orchestrating commands for Task ID: {}.", taskId);
    }
}
