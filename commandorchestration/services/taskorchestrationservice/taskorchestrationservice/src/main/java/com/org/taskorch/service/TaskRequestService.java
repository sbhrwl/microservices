package com.org.taskorch.service;

import com.org.taskorch.document.TaskRequest;
import com.org.taskorch.repository.TaskRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TaskRequestService {

    private final TaskRequestRepository taskRequestRepository;

    @Autowired
    public TaskRequestService(TaskRequestRepository taskRequestRepository) {
        this.taskRequestRepository = taskRequestRepository;
    }

    // Get all task requests
    public List<TaskRequest> getAllTaskRequests() {
        return taskRequestRepository.findAll();
    }

    // Get a single task request by ID
    public Optional<TaskRequest> getTaskRequestById(String id) {
        return taskRequestRepository.findById(id);
    }

    // Additional service methods (e.g., create, update, delete) can be added here
}

