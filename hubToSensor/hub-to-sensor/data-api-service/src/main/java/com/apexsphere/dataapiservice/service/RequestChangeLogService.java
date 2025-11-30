package com.apexsphere.dataapiservice.service;

import com.apexsphere.dataapiservice.dto.ChangeLogDTO;
import com.apexsphere.dataapiservice.model.RequestChangeLog;
import com.apexsphere.dataapiservice.repository.RequestChangeLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RequestChangeLogService {

    private final RequestChangeLogRepository changeLogRepository;

    public RequestChangeLogService(RequestChangeLogRepository changeLogRepository) {
        this.changeLogRepository = changeLogRepository;
    }

    /**
     * API 2: Request Status Details
     * Retrieves all Change Logs for a given Control Request ID.
     */
    public List<ChangeLogDTO> getChangeLogsByRequestId(Long requestId) {
        List<RequestChangeLog> logs = changeLogRepository.findByControlRequestId(requestId);

        // Map the list of entities to DTOs
        return logs.stream()
                .map(this::mapToChangeLogDTO)
                .collect(Collectors.toList());
    }

    // --- Mapping Method (private) ---
    private ChangeLogDTO mapToChangeLogDTO(RequestChangeLog log) {
        return new ChangeLogDTO(
            log.getId(),
            log.getDescription(),        // use 'description' instead of 'changeDescription'
            log.getChangeTimestamp()
        );
    }
}
