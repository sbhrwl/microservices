package com.apexsphere.dataapiservice.repository;

import com.apexsphere.dataapiservice.model.ControlRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlRequestRepository extends JpaRepository<ControlRequest, Long> {
    // Spring Data JPA automatically provides methods like save(), findById(), and findAll().
    // No custom code is required here for API 1 (Request Details) or API 3 (Request Tracker).
}