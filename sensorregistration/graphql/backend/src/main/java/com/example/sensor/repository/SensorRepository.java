package com.example.sensor.repository;

import com.example.sensor.domain.Sensor;
import java.util.List;
import java.util.Optional;

/**
 * Repository contract - belongs to domain boundary.
 * No framework dependencies.
 */
public interface SensorRepository {
    
    /**
     * Save or update a sensor.
     */
    Sensor save(Sensor sensor);
    
    /**
     * Find sensor by its unique ID.
     */
    Optional<Sensor> findBySensorId(String sensorId);
    
    /**
     * Find all sensors for a given user.
     */
    List<Sensor> findByUserEmail(String userEmail);
    
    /**
     * Check if sensor exists.
     */
    boolean existsBySensorId(String sensorId);
}