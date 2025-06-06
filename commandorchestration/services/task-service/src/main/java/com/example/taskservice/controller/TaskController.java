package com.example.taskservice.controller;

import com.example.taskservice.model.Task;
import com.example.taskservice.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Keep this import
import org.slf4j.Logger; // Keep this import
import org.slf4j.LoggerFactory; // Keep this import


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    // Existing POST endpoint (with diagnostic logging)
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("Authenticated user: {}", authentication.getName());
            logger.info("Authorities: {}", authentication.getAuthorities());
        } else {
            logger.warn("Request to /tasks is not authenticated.");
        }
        return ResponseEntity.ok(taskService.createTask(task));
    }

    // NEW PUBLIC ENDPOINT
    @GetMapping("/public") // <--- ADD THIS NEW METHOD
    public ResponseEntity<String> getPublicTask() {
        logger.info("Accessing public /tasks/public endpoint.");
        return ResponseEntity.ok("This is a public endpoint from task-service!");
    }
}