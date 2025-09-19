package com.apexsphere.storage_service.controller;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.service.RecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<String> createRecord(@RequestBody Record record) {
        recordService.saveRecord(record);
        System.out.println("Message saved successfully");
        return new ResponseEntity<>("Message saved successfully", HttpStatus.CREATED);
    }
}