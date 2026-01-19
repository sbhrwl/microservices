package com.example.sensor.repository;

import com.example.sensor.domain.Sensor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of SensorRepository.
 * Translates MongoDB exceptions to domain exceptions.
 */
@Repository
public class MongoSensorRepository implements SensorRepository {
    
    private final SpringDataSensorRepository springDataRepository;

    public MongoSensorRepository(SpringDataSensorRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Sensor save(Sensor sensor) {
        try {
            SensorDocument document = new SensorDocument(sensor);
            SensorDocument saved = springDataRepository.save(document);
            return saved.toDomain();
        } catch (DuplicateKeyException e) {
            throw new SensorAlreadyExistsException(
                "Sensor with ID '" + sensor.getSensorId() + "' already exists"
            );
        } catch (Exception e) {
            throw new SensorRepositoryException("Failed to save sensor", e);
        }
    }

    @Override
    public Optional<Sensor> findBySensorId(String sensorId) {
        try {
            return springDataRepository.findBySensorId(sensorId)
                .map(SensorDocument::toDomain);
        } catch (Exception e) {
            throw new SensorRepositoryException("Failed to find sensor", e);
        }
    }

    @Override
    public List<Sensor> findByUserEmail(String userEmail) {
        try {
            return springDataRepository.findByUserEmail(userEmail)
                .stream()
                .map(SensorDocument::toDomain)
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new SensorRepositoryException("Failed to find sensors by user", e);
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