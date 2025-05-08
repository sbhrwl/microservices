package com.example.postservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.List;

@Document(collection = "command_requests")
public class CommandRequest {

    @Id
    private String correlationId; // Use correlationId as the primary key
    private String requestedBy;
    private List<String> deviceIds;
    private String commandType;
    private CommandParams commandParams;
    private ZonedDateTime scheduledAt;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
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

    public ZonedDateTime getScheduledAt() { // Updated getter
        return scheduledAt;
    }

    public void setScheduledAt(ZonedDateTime scheduledAt) { // Updated setter
        this.scheduledAt = scheduledAt;
    }

    public static class CommandParams {
        private String touProfileId;

        public String getTouProfileId() {
            return touProfileId;
        }

        public void setTouProfileId(String touProfileId) {
            this.touProfileId = touProfileId;
        }
    }
}