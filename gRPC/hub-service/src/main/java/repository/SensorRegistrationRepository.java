package com.example.hubservice.repository;

import com.example.hubservice.entity.SensorRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * SensorRegistrationRepository is a Spring Data JPA Repository interface.
 * It provides standard CRUD (Create, Read, Update, Delete) operations
 * for the SensorRegistration entity, mapped to the 'sensor_registrations' table.
 * Spring Data JPA automatically generates the implementation at runtime.
 */
@Repository // Marks this interface as a Spring Data JPA repository
public interface SensorRegistrationRepository extends JpaRepository<SensorRegistration, Long> {

    // You can define custom query methods here if needed, e.g.:
    // Optional<SensorRegistration> findBySensorId(String sensorId);
}
