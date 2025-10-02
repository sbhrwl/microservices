package com.apexsphere.storage_service.service;

import com.apexsphere.storage_service.model.Record;
import com.apexsphere.storage_service.model.RequestChangeLog; // New Import
import com.apexsphere.storage_service.repository.RecordRepository;
import com.apexsphere.storage_service.repository.RequestChangeLogRepository; // New Import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added for transactional safety

import java.util.Optional;

@Service
public class RecordService {

    private final RecordRepository recordRepository;
    private final RequestChangeLogRepository changeLogRepository; // New Dependency

    // Constructor updated to include the new repository
    public RecordService(RecordRepository recordRepository, RequestChangeLogRepository changeLogRepository) {
        this.recordRepository = recordRepository;
        this.changeLogRepository = changeLogRepository;
    }

    @Transactional // Ensures both save and log entry succeed or fail together
    public Record saveRecord(Record record) {
        // Add business logic, validation, etc. before saving
        Record savedRecord = recordRepository.save(record);

        // 1. Log the creation
        RequestChangeLog log = new RequestChangeLog(
            savedRecord.getId(),
            "Request created"
        );
        changeLogRepository.save(log);

        return savedRecord;
    }
    
    // Updates the status of an existing record
    @Transactional // Ensures both update and log entry succeed or fail together
    public Record updateRecordStatus(Record record) {
        
        // 1. Find the existing record (Example: find by ID if the Record object contains it)
        Optional<Record> existingRecordOpt = recordRepository.findById(record.getId());
        
        if (existingRecordOpt.isEmpty()) {
            // Handle error: Record not found
            throw new RuntimeException("Record not found for update.");
        }
        
        Record existingRecord = existingRecordOpt.get();
        String oldStatus = existingRecord.getStatus();

        // 2. Update only the status field
        existingRecord.setStatus(record.getStatus());
        
        // 3. Save the updated record
        Record updatedRecord = recordRepository.save(existingRecord);

        // 4. Log the update
        RequestChangeLog log = new RequestChangeLog(
            updatedRecord.getId(),
            String.format("Status updated from '%s' to '%s'", oldStatus, updatedRecord.getStatus())
        );
        changeLogRepository.save(log);

        return updatedRecord;
    }
}