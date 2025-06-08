package com.example.commandorchestrator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "commands") // Designates this as a MongoDB document
public class Command {

    @Id // Marks this field as the primary identifier in MongoDB
    private String id; // Unique ID for the command itself (different from taskId)
    private String taskId; // Reference back to the original task
    private String sensorId; // The specific sensor this command is for
    private String commandType; // e.g., START, STOP, RESTART
    private List<String> commandArgs; // Arguments for the command
    private LocalDateTime createdAt; // Timestamp when the command was created

    // Constructor
    public Command(String taskId, String sensorId, String commandType, List<String> commandArgs) {
        this.taskId = taskId;
        this.sensorId = sensorId;
        this.commandType = commandType;
        this.commandArgs = commandArgs;
        this.createdAt = LocalDateTime.now(); // Set creation time upon instantiation
    }

    // Default constructor for MongoDB deserialization
    public Command() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public List<String> getCommandArgs() {
        return commandArgs;
    }

    public void setCommandArgs(List<String> commandArgs) {
        this.commandArgs = commandArgs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Command{" +
               "id='" + id + '\'' +
               ", taskId='" + taskId + '\'' +
               ", sensorId='" + sensorId + '\'' +
               ", commandType='" + commandType + '\'' +
               ", commandArgs=" + commandArgs +
               ", createdAt=" + createdAt +
               '}';
    }
}