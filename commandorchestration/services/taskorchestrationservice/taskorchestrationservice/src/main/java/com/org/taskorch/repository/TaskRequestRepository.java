package com.org.taskorch.repository;

import com.org.taskorch.document.TaskRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRequestRepository extends MongoRepository<TaskRequest, String> {
}