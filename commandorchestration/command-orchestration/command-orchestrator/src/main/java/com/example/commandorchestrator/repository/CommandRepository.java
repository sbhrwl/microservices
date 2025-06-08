package com.example.commandorchestrator.repository;

import com.example.commandorchestrator.model.Command;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRepository extends MongoRepository<Command, String> {
    // Spring Data MongoDB will automatically provide CRUD operations
    // You can add custom query methods here if needed, e.g.,
    // List<Command> findByTaskId(String taskId);
}