package com.apexsphere.dataapiservice.repository;

import com.apexsphere.dataapiservice.model.RequestChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestChangeLogRepository extends JpaRepository<RequestChangeLog, Long> {

    // Fetch logs by control_request_id ordered by change_timestamp ascending
    List<RequestChangeLog> findByControlRequestIdOrderByChangeTimestampAsc(Long controlRequestId);
}
