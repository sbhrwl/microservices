package com.example.sensor.application;

import com.example.sensor.domain.Sensor;
import com.example.sensor.repository.SensorAlreadyExistsException;
import com.example.sensor.repository.SensorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Application service - orchestrates domain operations.
 * This is where business workflows live.
 */
@Service
@Transactional
public class SensorApplicationService {
    
    private final SensorRepository sensorRepository;

    public SensorApplicationService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    /**
     * Register a new sensor.
     * 
     * @throws SensorAlreadyExistsException if sensor ID already exists
     * @throws IllegalArgumentException if input validation fails
     */
    public Sensor registerSensor(String sensorId, String userEmail, String postcode) {
        // Check if sensor already exists
        if (sensorRepository.existsBySensorId(sensorId)) {
            throw new SensorAlreadyExistsException(
                "Sensor with ID '" + sensorId + "' already exists"
            );
        }

        // Create domain object (validates inputs)
        Sensor sensor = Sensor.register(sensorId, userEmail, postcode);
        
        // Persist
        return sensorRepository.save(sensor);
    }

    /**
     * Get sensor by ID.
     * 
     * @throws SensorNotFoundException if sensor doesn't exist
     */
    @Transactional(readOnly = true)
    public Sensor getSensor(String sensorId) {
        return sensorRepository.findBySensorId(sensorId)
            .orElseThrow(() -> new SensorNotFoundException(
                "Sensor with ID '" + sensorId + "' not found"
            ));
    }

    /**
     * List all sensors for a user.
     * 
     * @throws NoSensorsFoundException if user has no sensors
     */
    @Transactional(readOnly = true)
    public List<Sensor> listSensorsByUser(String userEmail) {
        List<Sensor> sensors = sensorRepository.findByUserEmail(userEmail);
        
        if (sensors.isEmpty()) {
            throw new NoSensorsFoundException(
                "No sensors found for user '" + userEmail + "'"
            );
        }
        
        return sensors;
    }

    /**
     * Update sensor postcode.
     * 
     * @throws SensorNotFoundException if sensor doesn't exist
     * @throws IllegalArgumentException if postcode is unchanged
     */
    public Sensor updateSensorPostcode(String sensorId, String newPostcode) {
        Sensor sensor = getSensor(sensorId);
        
        // Domain method enforces "must be different" rule
        sensor.updatePostcode(newPostcode);
        
        return sensorRepository.save(sensor);
    }
}