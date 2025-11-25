package com.apexsphere.storage_service.postgres.repository;

import com.apexsphere.storage_service.postgres.model.RequestChangeLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestChangeLogJPARepository extends JpaRepository<RequestChangeLogEntity, Long> {
}
