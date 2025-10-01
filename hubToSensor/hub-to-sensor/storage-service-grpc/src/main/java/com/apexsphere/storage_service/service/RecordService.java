package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.repository.RecordRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public Record saveRecord(Record record) {
        // Add business logic, validation, etc. before saving
        return recordRepository.save(record);
    }
    
    // Updates the status of an existing record
    public Record updateRecordStatus(Record record) {
        // Assuming the 'Record' object passed here contains the unique ID 
        // (e.g., record.getId() or record.getSensorId() + other keys) 
        // needed to find the existing entry.
        
        // 1. Find the existing record (Example: find by ID if the Record object contains it)
        Optional<Record> existingRecordOpt = recordRepository.findById(record.getId());
        
        if (existingRecordOpt.isEmpty()) {
            // Handle error: Record not found
            throw new RuntimeException("Record not found for update.");
        }
        
        Record existingRecord = existingRecordOpt.get();

        // 2. Update only the status field
        existingRecord.setStatus(record.getStatus());
        
        // 3. Save the updated record
        return recordRepository.save(existingRecord);
    }
}