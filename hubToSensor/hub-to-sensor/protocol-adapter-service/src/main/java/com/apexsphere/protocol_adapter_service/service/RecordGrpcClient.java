package com.apexsphere.protocol_adapter_service.service;

import com.apexsphere.storage_service.service.RecordRequest;
import com.apexsphere.storage_service.service.RecordResponse;
import com.apexsphere.storage_service.service.RecordServiceGrpc;
import org.springframework.stereotype.Service;

@Service
public class RecordGrpcClient {

    private final RecordServiceGrpc.RecordServiceBlockingStub blockingStub;
    
    public RecordGrpcClient(RecordServiceGrpc.RecordServiceBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    /**
     * Saves the initial record to the Storage Service and returns the generated
     * record ID, which is necessary for future updates.
     * * @param request The RecordRequest payload.
     * @return The unique String ID of the newly saved record.
     */
    public String saveRecord(RecordRequest request) {
        RecordResponse response = blockingStub.saveRecord(request);
        // CRITICAL CHANGE: Return the generated ID instead of the message.
        return response.getRecordId(); 
    }

    /**
     * Updates the status of an existing record using the record ID provided in the request.
     * * @param request The RecordRequest containing the ID and the new status.
     * @return A status message from the server.
     */
    public String updateRecordStatus(RecordRequest request) {
        RecordResponse response = blockingStub.updateRecord(request); 
        return response.getMessage();
    }
}
