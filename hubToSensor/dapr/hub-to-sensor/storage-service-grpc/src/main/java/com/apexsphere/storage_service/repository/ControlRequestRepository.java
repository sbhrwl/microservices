package com.apexsphere.storage_service.postgres.repository;

import com.apexsphere.storage_service.postgres.model.ControlRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ControlRequestRepository extends JpaRepository<ControlRequestEntity, Long> {
}
