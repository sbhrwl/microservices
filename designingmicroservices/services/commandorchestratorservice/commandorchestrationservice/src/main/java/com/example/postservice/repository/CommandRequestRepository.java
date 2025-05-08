package com.example.postservice.repository;

import com.example.postservice.model.CommandRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRequestRepository extends MongoRepository<CommandRequest, String> {
}