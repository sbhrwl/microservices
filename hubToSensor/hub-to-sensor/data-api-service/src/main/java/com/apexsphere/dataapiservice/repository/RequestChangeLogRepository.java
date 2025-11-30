package com.apexsphere.dataapiservice.repository;

import com.apexsphere.dataapiservice.model.RequestChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestChangeLogRepository extends JpaRepository<RequestChangeLog, Long> {

    /**
     * Finds all change logs associated with a specific Control Request ID.
     */
    List<RequestChangeLog> findByControlRequestId(Long controlRequestId);
}
