package com.apexsphere.dataapiservice.controller;

import com.apexsphere.dataapiservice.dto.ChangeLogDTO;
import com.apexsphere.dataapiservice.dto.ControlRequestDTO;
import com.apexsphere.dataapiservice.dto.RequestTrackerDTO;
import com.apexsphere.dataapiservice.service.ControlRequestService;
import com.apexsphere.dataapiservice.service.RequestChangeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
public class ControlRequestController {

    private static final Logger log = LoggerFactory.getLogger(ControlRequestController.class);

    private final ControlRequestService controlRequestService;
    private final RequestChangeLogService changeLogService;

    public ControlRequestController(
            ControlRequestService controlRequestService,
            RequestChangeLogService changeLogService) {

        this.controlRequestService = controlRequestService;
        this.changeLogService = changeLogService;
    }

    /**
     * API 1: Request Details
     * Endpoint: GET /api/v1/requests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ControlRequestDTO> getRequestDetails(@PathVariable Long id) {
        ControlRequestDTO dto = controlRequestService.getRequestDetails(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * API 2: Request Status Details
     * Endpoint: GET /api/v1/requests/{id}/logs
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<ChangeLogDTO>> getRequestStatusDetails(@PathVariable Long id) {
        log.info("➡️ [Controller] Received request for logs of request_id={}", id);

        List<ChangeLogDTO> logs = changeLogService.getChangeLogsByRequestId(id);

        log.info("⬅️ [Controller] Returning {} log entries for request_id={}",
                 logs != null ? logs.size() : null,
                 id);

        return ResponseEntity.ok(logs);
    }

    /**
     * API 3: Request Tracker
     */
    @GetMapping("/{id}/tracker")
    public ResponseEntity<RequestTrackerDTO> getRequestTracker(@PathVariable Long id) {
        RequestTrackerDTO dto = controlRequestService.getRequestTracker(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * API 0: All Request Details (New)
     */
    @GetMapping
    public ResponseEntity<List<ControlRequestDTO>> getAllRequests() {
        List<ControlRequestDTO> dtoList = controlRequestService.getAllRequestDetails();
        return ResponseEntity.ok(dtoList);
    }
}
