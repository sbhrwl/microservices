package com.apexsphere.flexibility_bridge_service.service;

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

    public String saveRecord(RecordRequest request) {
        RecordResponse response = blockingStub.saveRecord(request);
        return response.getMessage();
    }

    public String updateRecordStatus(RecordRequest request) {
    RecordResponse response = blockingStub.updateRecord(request); 
    return response.getMessage();
    }
}