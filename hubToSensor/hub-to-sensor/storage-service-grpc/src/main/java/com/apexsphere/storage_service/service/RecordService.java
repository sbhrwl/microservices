package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.repository.RecordRepository;
import org.springframework.stereotype.Service;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public Record saveRecord(Record record) {
        // Here you can add business logic, validation, etc. before saving
        return recordRepository.save(record);
    }
}