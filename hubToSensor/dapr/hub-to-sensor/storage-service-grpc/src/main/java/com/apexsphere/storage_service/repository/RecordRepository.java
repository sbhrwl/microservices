package com.apexsphere.storage_service.repository;

import com.apexsphere.storage_service.model.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
