package com.example.sensor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository interface.
 */
public interface SpringDataSensorRepository extends MongoRepository<SensorDocument, String> {
    
    Optional<SensorDocument> findBySensorId(String sensorId);
    
    List<SensorDocument> findByUserEmail(String userEmail);
    
    boolean existsBySensorId(String sensorId);
}