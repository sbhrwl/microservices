package com.org.taskorch.document;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "command_requests") // MongoDB collection name
public class TaskRequest {

    @Id
    @JsonProperty("command_id") // Maps JSON property to this field
    private String taskId; // Use As Task is not yet created, so taskId mapped to command_id column of database

    @JsonProperty("requested_by")
    private String requestedBy;

    @JsonProperty("device_ids")
    private List<String> deviceIds;

    @JsonProperty("command_type")
    private String commandType;

    @JsonProperty("command_params")
    private CommandParams commandParams;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public List<String> getDeviceIds() {
        return deviceIds;
    }

    public void setDeviceIds(List<String> deviceIds) {
        this.deviceIds = deviceIds;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public CommandParams getCommandParams() {
        return commandParams;
    }

    public void setCommandParams(CommandParams commandParams) {
        this.commandParams = commandParams;
    }

    @Override
    public String toString() {
        return "CommandRequest{" +
                "taskId='" + taskId + '\'' +
                ", requestedBy='" + requestedBy + '\'' +
                ", deviceIds=" + deviceIds +
                ", commandType='" + commandType + '\'' +
                ", commandParams=" + commandParams +
                '}';
    }

    public static class CommandParams {

        @JsonProperty("tou_profile_id")
        private String touProfileId;

        public String getTouProfileId() {
            return touProfileId;
        }

        public void setTouProfileId(String touProfileId) {
            this.touProfileId = touProfileId;
        }

        @Override
        public String toString() {
            return "CommandParams{" +
                    "touProfileId='" + touProfileId + '\'' +
                    '}';
        }
    }
}