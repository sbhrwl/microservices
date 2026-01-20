package com.example.sensor.repository;

import com.example.sensor.domain.Sensor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MongoSensorRepository implements SensorRepository {
    
    private final SpringDataSensorRepository springDataRepository;

    public MongoSensorRepository(SpringDataSensorRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Sensor save(Sensor sensor) {
        try {
            // Find existing document to preserve MongoDB _id
            Optional<SensorDocument> existingDoc = springDataRepository.findBySensorId(sensor.getSensorId());
            
            SensorDocument document;
            if (existingDoc.isPresent()) {
                // Update existing document (preserves _id)
                document = existingDoc.get();
                document.setUserEmail(sensor.getUserEmail());
                document.setPostcode(sensor.getPostcode());
                document.setStatus(sensor.getStatus().name());
                document.setUpdatedAt(sensor.getLastUpdatedAt());
                // Don't update createdAt - it should remain unchanged
            } else {
                // Create new document
                document = new SensorDocument(sensor);
            }
            
            SensorDocument saved = springDataRepository.save(document);
            return saved.toDomain();
        } catch (Exception e) {
            throw new SensorRepositoryException("Failed to save sensor", e);
        }
    }

    @Override
    public Optional<Sensor> findBySensorId(String sensorId) {
        try {
            return springDataRepository.findBySensorId(sensorId)
                .map(SensorDocument::toDomain);
        } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
            throw new SensorRepositoryException(
                "Data integrity violation: Multiple sensors found with ID: " + sensorId + 
                ". Please check database constraints.", e);
        } catch (Exception e) {
            throw new SensorRepositoryException("Failed to find sensor", e);
        }
    }

    @Override
    public List<Sensor> findByUserEmail(String userEmail) {
        try {
            return springDataRepository.findByUserEmail(userEmail).stream()
                .map(SensorDocument::toDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new SensorRepositoryException("Failed to find sensors by user email", e);
        }
    }

    @Override
    public boolean existsBySensorId(String sensorId) {
        try {
            return springDataRepository.existsBySensorId(sensorId);
        } catch (Exception e) {
            throw new SensorRepositoryException("Failed to check sensor existence", e);
        }
    }
}