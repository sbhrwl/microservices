package com.example.commandorchestrator.service;

import com.example.commandorchestrator.model.Command;
import com.example.commandorchestrator.repository.CommandRepository;
import com.example.taskservice.TaskMessageProto; // For the incoming Protobuf message
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // To read properties
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommandOrchestrationService {

    private static final Logger logger = LoggerFactory.getLogger(CommandOrchestrationService.class);

    private final CommandRepository commandRepository;

    @Value("${command.persistence.enabled:true}") // Read the configurable property, default to true
    private boolean commandPersistenceEnabled;

    public CommandOrchestrationService(CommandRepository commandRepository) {
        this.commandRepository = commandRepository;
    }

    public void orchestrateTaskCommands(TaskMessageProto.TaskMessage taskMessage) {
        String taskId = taskMessage.getTaskId();
        // Corrected line: Use getSensorListList() for the list of sensors
        List<String> sensorList = taskMessage.getSensorListList();
        String commandType = taskMessage.getCommandType().name(); // Get string name from enum
        List<String> commandArgs = taskMessage.getCommandArgsList(); // This one was already correct

        logger.info("Orchestrating commands for Task ID: {} with {} sensors.", taskId, sensorList.size());

        for (String sensorId : sensorList) {
            // Create a Command object for each sensor
            Command command = new Command(taskId, sensorId, commandType, commandArgs);
            logger.debug("Generated command for sensor {}: {}", sensorId, command.toString());

            if (commandPersistenceEnabled) {
                // Save the command to MongoDB
                Command savedCommand = commandRepository.save(command);
                logger.info("Saved command to MongoDB for sensor {} with ID: {}", sensorId, savedCommand.getId());
            } else {
                logger.info("Command persistence is disabled. Skipping saving command for sensor {} to MongoDB.", sensorId);
            }

            // TODO: In the next step, we will add the logic here to call the sensor endpoint
            logger.info("Command for sensor {} is ready for execution. (Execution to be implemented next)", sensorId);
        }

        logger.info("Finished orchestrating commands for Task ID: {}.", taskId);
    }
}