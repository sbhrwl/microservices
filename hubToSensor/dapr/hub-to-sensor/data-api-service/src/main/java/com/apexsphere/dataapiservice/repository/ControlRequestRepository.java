package com.apexsphere.dataapiservice.repository;

import com.apexsphere.dataapiservice.model.ControlRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControlRequestRepository extends JpaRepository<ControlRequest, Long> {
    List<ControlRequest> findAllByOrderByIdDesc();
}
