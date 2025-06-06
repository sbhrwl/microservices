
package com.example.taskservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tasks")
public class Task {

    @Id
    private String taskId;
    private CommandType commandType;
    private List<String> commandArgs;
    private List<String> sensorList;

    // Getters and setters
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public CommandType getCommandType() { return commandType; }
    public void setCommandType(CommandType commandType) { this.commandType = commandType; }

    public List<String> getCommandArgs() { return commandArgs; }
    public void setCommandArgs(List<String> commandArgs) { this.commandArgs = commandArgs; }

    public List<String> getSensorList() { return sensorList; }
    public void setSensorList(List<String> sensorList) { this.sensorList = sensorList; }
}
