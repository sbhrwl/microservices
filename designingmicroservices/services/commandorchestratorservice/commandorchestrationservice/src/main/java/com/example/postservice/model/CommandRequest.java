package com.example.postservice.model;

import java.util.List;

public class CommandRequest {
    private String correlationId;
    private String requestedBy;
    private List<String> deviceIds;
    private String commandType;
    private CommandParams commandParams;
    private String scheduledAt;

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

    public String getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(String scheduledAt) {
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