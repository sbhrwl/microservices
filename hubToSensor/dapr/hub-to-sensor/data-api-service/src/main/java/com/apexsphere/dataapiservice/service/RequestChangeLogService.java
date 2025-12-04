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
     * Retrieves all Change Logs for a given Control Request ID, ordered by timestamp ascending.
     */
    public List<ChangeLogDTO> getChangeLogsByRequestId(Long requestId) {
        List<RequestChangeLog> logs = changeLogRepository
                .findByControlRequestIdOrderByChangeTimestampAsc(requestId); // <-- updated

        return logs.stream()
                .map(this::mapToChangeLogDTO)
                .collect(Collectors.toList());
    }

    private ChangeLogDTO mapToChangeLogDTO(RequestChangeLog log) {
        return new ChangeLogDTO(
                log.getId(),
                log.getDescription(), // make sure it matches DB column
                log.getChangeTimestamp()
        );
    }
}
