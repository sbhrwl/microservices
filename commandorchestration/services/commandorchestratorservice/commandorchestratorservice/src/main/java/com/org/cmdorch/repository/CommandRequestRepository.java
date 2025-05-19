package com.org.cmdorch.repository;

import com.org.cmdorch.document.CommandRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandRequestRepository extends MongoRepository<CommandRequest, String> {
}