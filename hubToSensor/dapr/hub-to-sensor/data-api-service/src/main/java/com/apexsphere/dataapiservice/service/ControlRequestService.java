package com.apexsphere.dataapiservice.service;

import com.apexsphere.dataapiservice.dto.ChangeLogDTO;
import com.apexsphere.dataapiservice.dto.ControlRequestDTO;
import com.apexsphere.dataapiservice.dto.RequestTrackerDTO;
import com.apexsphere.dataapiservice.exception.ResourceNotFoundException;
import com.apexsphere.dataapiservice.model.ControlRequest;
import com.apexsphere.dataapiservice.repository.ControlRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List; // <-- FIXED: Added missing List import
import java.util.stream.Collectors;

@Service
public class ControlRequestService {

    private final ControlRequestRepository controlRequestRepository;

    public ControlRequestService(ControlRequestRepository controlRequestRepository) {
        this.controlRequestRepository = controlRequestRepository;
    }

    /**
     * API 1: Request Details
     * Retrieves a ControlRequest by ID and maps it to a ControlRequestDTO.
     */
    public ControlRequestDTO getRequestDetails(Long id) {
        ControlRequest request = controlRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Control Request not found with ID: " + id));

        return mapToControlRequestDTO(request);
    }

    /**
     * API 3: Request Tracker
     * Retrieves the ControlRequest and its associated ChangeLogs (using the JPA @OneToMany mapping)
     * and maps them to a RequestTrackerDTO.
     */
    @Transactional(readOnly = true)
    public RequestTrackerDTO getRequestTracker(Long id) {
        // FindById is sufficient; the @OneToMany relationship will load the logs (depending on FetchType).
        ControlRequest request = controlRequestRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Control Request not found with ID: " + id));

        return mapToRequestTrackerDTO(request);
    }

    /**
     * Retrieves ALL Control Requests and maps them to a list of DTOs.
     * Required for the initial list view.
     */
    @Transactional(readOnly = true)
    public List<ControlRequestDTO> getAllRequestDetails() {
        return controlRequestRepository.findAll().stream()
            .map(this::mapToControlRequestDTO)
            .collect(Collectors.toList());
    }

    // --- Mapping Methods (private) ---

    private ControlRequestDTO mapToControlRequestDTO(ControlRequest request) {
        return new ControlRequestDTO(
            request.getId(),
            request.getDuration(),
            request.getOperation(),
            request.getRelayNumber(),
            request.getSensorId(),
            request.getStatus()
        );
    }
    
    private RequestTrackerDTO mapToRequestTrackerDTO(ControlRequest request) {
        RequestTrackerDTO dto = new RequestTrackerDTO();
        dto.setId(request.getId());
        dto.setDuration(request.getDuration());
        dto.setOperation(request.getOperation());
        dto.setRelayNumber(request.getRelayNumber());
        dto.setSensorId(request.getSensorId());
        dto.setStatus(request.getStatus());

        // Map the List of ChangeLog Entities to a List of ChangeLogDTOs
        if (request.getChangeLogs() != null) {
            dto.setChangeLogs(request.getChangeLogs().stream()
                .map(this::mapToChangeLogDTO)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    private ChangeLogDTO mapToChangeLogDTO(com.apexsphere.dataapiservice.model.RequestChangeLog log) {
        return new ChangeLogDTO(
            log.getId(),
            log.getChangeDescription(),
            log.getChangeTimestamp()
        );
    }
}