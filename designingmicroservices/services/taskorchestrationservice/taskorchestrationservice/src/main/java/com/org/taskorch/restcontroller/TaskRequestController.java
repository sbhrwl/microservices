package com.org.taskorch.restcontroller;

import com.org.taskorch.document.TaskRequest;
import com.org.taskorch.service.TaskRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks") // Base URL for all task-related endpoints
public class TaskRequestController {

    private final TaskRequestService taskRequestService;

    @Autowired
    public TaskRequestController(TaskRequestService taskRequestService) {
        this.taskRequestService = taskRequestService;
    }

    // Get all task requests
    @GetMapping
    public ResponseEntity<List<TaskRequest>> getAllTaskRequests() {
        List<TaskRequest> tasks = taskRequestService.getAllTaskRequests();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    // Get a single task request by ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<TaskRequest>> getTaskRequestById(@PathVariable String id) {
        Optional<TaskRequest> task = taskRequestService.getTaskRequestById(id);
        if (task.isPresent()) {
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Or .NOT_FOUND
        }
    }

    // Additional controller methods (e.g., create, update, delete) will be added here
}

