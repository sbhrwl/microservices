package com.example.taskservice.service;

import com.example.taskservice.model.Task;
import com.example.taskservice.TaskMessageProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtoMapper {

    private static final Logger logger = LoggerFactory.getLogger(ProtoMapper.class);

    public static TaskMessageProto.TaskMessage toProto(Task task) {
        logger.info("Mapping Task object to Proto TaskMessage for Task ID: {}", task.getTaskId());

        TaskMessageProto.TaskMessage protoMessage = TaskMessageProto.TaskMessage.newBuilder()
                .setTaskId(task.getTaskId())
                .setCommandType(TaskMessageProto.CommandType.valueOf(task.getCommandType().name()))
                .addAllCommandArgs(task.getCommandArgs())
                .addAllSensorList(task.getSensorList())
                .build();

        // New log to show the converted Protobuf message content
        logger.info("Converted Protobuf message for Task ID {}: {}", task.getTaskId(), protoMessage.toString());

        return protoMessage;
    }
}