package com.apexsphere.storage_service.repository;

import com.apexsphere.storage_service.model.RequestChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestChangeLogRepository extends JpaRepository<RequestChangeLog, Long> {
    // Basic CRUD operations are inherited
}