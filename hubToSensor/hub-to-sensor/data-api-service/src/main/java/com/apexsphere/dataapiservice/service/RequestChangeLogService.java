package com.apexsphere.dataapiservice.service;

import com.apexsphere.dataapiservice.dto.ChangeLogDTO;
import com.apexsphere.dataapiservice.exception.ResourceNotFoundException;
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
     * Retrieves all Change Logs for a given Control Request ID (recordId).
     */
    public List<ChangeLogDTO> getChangeLogsByRequestId(Long requestId) {
        List<RequestChangeLog> logs = changeLogRepository.findByRecordId(requestId);

        if (logs.isEmpty()) {
            // While a request may exist without logs, 
            // for API 2 specifically targeting logs, we can return 404 if the list is empty, 
            // or an empty list. We'll throw an exception if the parent request is expected to exist.
            // For now, we'll assume a request is expected, and if no logs, it should be handled 
            // by a broader check or return empty. Since the plan is 404, we'll throw 
            // if we assume existence. A simpler approach is to throw 404 only if the 
            // parent request does not exist, but let's stick to the current scope.
            
            // To be robust, let's assume if logs are empty, we return an empty list [], 
            // unless we confirm the parent request doesn't exist (which would involve the ControlRequestService).
            // For conciseness, we just map the results.
        }

        // Map the list of entities to a list of DTOs
        return logs.stream()
                .map(this::mapToChangeLogDTO)
                .collect(Collectors.toList());
    }

    // --- Mapping Method (private) ---

    private ChangeLogDTO mapToChangeLogDTO(RequestChangeLog log) {
        return new ChangeLogDTO(
            log.getId(),
            log.getChangeDescription(),
            log.getChangeTimestamp()
        );
    }
}