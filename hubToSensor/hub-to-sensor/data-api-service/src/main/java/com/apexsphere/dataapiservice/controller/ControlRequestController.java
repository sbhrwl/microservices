package com.apexsphere.dataapiservice.controller;

import com.apexsphere.dataapiservice.dto.ChangeLogDTO;
import com.apexsphere.dataapiservice.dto.ControlRequestDTO;
import com.apexsphere.dataapiservice.dto.RequestTrackerDTO;
import com.apexsphere.dataapiservice.service.ControlRequestService;
import com.apexsphere.dataapiservice.service.RequestChangeLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
public class ControlRequestController {

    private final ControlRequestService controlRequestService;
    private final RequestChangeLogService changeLogService;

    public ControlRequestController(ControlRequestService controlRequestService, RequestChangeLogService changeLogService) {
        this.controlRequestService = controlRequestService;
        this.changeLogService = changeLogService;
    }

    /**
     * API 1: Request Details
     * Endpoint: GET /api/v1/requests/{id}
     * Returns HTTP 200 OK or 404 Not Found (handled by GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    public ResponseEntity<ControlRequestDTO> getRequestDetails(@PathVariable Long id) {
        ControlRequestDTO dto = controlRequestService.getRequestDetails(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * API 2: Request Status Details
     * Endpoint: GET /api/v1/requests/{id}/logs
     * Returns all change logs for a given request.
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<ChangeLogDTO>> getRequestStatusDetails(@PathVariable Long id) {
        // We use the separate service to retrieve logs by the request's record_id
        List<ChangeLogDTO> logs = changeLogService.getChangeLogsByRequestId(id);
        
        // Note: For API 2, returning an empty list [] is generally preferred over 404 
        // if the request (parent) exists but has no logs yet.
        return ResponseEntity.ok(logs);
    }

    /**
     * API 3: Request Tracker
     * Endpoint: GET /api/v1/requests/{id}/tracker
     * Returns the parent request and its children logs in a single DTO.
     */
    @GetMapping("/{id}/tracker")
    public ResponseEntity<RequestTrackerDTO> getRequestTracker(@PathVariable Long id) {
        RequestTrackerDTO dto = controlRequestService.getRequestTracker(id);
        return ResponseEntity.ok(dto);
    }
}