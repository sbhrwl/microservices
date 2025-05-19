package com.example.registrationservice.repository;

import com.example.registrationservice.document.Registration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationRepository extends MongoRepository<Registration, String> {
}