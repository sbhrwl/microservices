package com.example.taskservice.service;

import com.example.taskservice.model.Task;
import com.example.taskservice.repository.TaskRepository;
import com.example.taskservice.TaskMessageProto.TaskMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    public Task createTask(Task task) {
        logger.info("Attempting to create task: {}", task.getTaskId());
        Task saved = taskRepository.save(task);
        logger.info("Task saved to MongoDB: {}", saved.getTaskId());

        TaskMessage protoMessage = ProtoMapper.toProto(saved);
        kafkaProducer.sendMessage(saved.getTaskId(), protoMessage.toByteArray());
        logger.info("Task message sent to Kafka for task ID: {}", saved.getTaskId());

        return saved;
    }
}