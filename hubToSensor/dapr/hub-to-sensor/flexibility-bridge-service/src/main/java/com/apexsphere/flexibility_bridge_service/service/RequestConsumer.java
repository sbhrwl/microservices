package com.apexsphere.flexibility_bridge_service.service;

import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apexsphere.flexibility_bridge_service.model.RequestPayload;
import com.apexsphere.storage_service.service.RecordRequest;

@RestController
public class RequestConsumer {

    private static final Logger log = LoggerFactory.getLogger(RequestConsumer.class);

    private final RecordGrpcClient grpcClient;
    private final RequestProducerForProtocolConversionService producerService;

    public RequestConsumer(RecordGrpcClient grpcClient, RequestProducerForProtocolConversionService producerService) {
        this.grpcClient = grpcClient;
        this.producerService = producerService;
    }

    // ✅ Consume from flexibility-hub.request (hub-request-topic)
    @Topic(name = "${messaging.dapr.hub-request-topic}", pubsubName = "${messaging.dapr.pubsub-name}")
    @PostMapping(path = "/flexibility-hub.request")
    public void receiveRequest(@RequestBody(required = false) CloudEvent<RequestPayload> cloudEvent) {
        if (cloudEvent == null || cloudEvent.getData() == null) {
            log.warn("⚠️ Received empty CloudEvent data — ignoring message.");
            return;
        }

        RequestPayload payload = cloudEvent.getData();
        log.info("✅ Received request for Sensor ID: {}", payload.getSensorId());

        String recordId = null;

        try {
            // 1️⃣ Save to control_request table
            RecordRequest controlRequest = convertToGrpcRequest(payload, "Control Requested", null);
            recordId = grpcClient.saveRecord(controlRequest);
            log.info("➡️ Saved Control Request. Status: 'Control Requested'. Record ID: {}", recordId);

            // 2️⃣ Save initial audit entry in change_request_log
            RecordRequest logEntry = convertToGrpcRequest(payload, "Initial Log Entry", recordId);
            grpcClient.saveRecord(logEntry);
            log.info("🪵 Created initial Change Request Log for Record ID: {}", recordId);

            // 3️⃣ Publish to connector.request
            producerService.sendRequestToConnector(payload, recordId);
            log.info("📢 Published request to connector. Record ID: {}", recordId);

            // 4️⃣ Update control_request status
            RecordRequest updateRequest = convertToGrpcRequest(payload, "Sent for protocol conversion", recordId);
            grpcClient.updateRecordStatus(updateRequest);
            log.info("🔁 Updated Control Request status to 'Sent for protocol conversion'. Record ID: {}", recordId);

            // 5️⃣ Add final audit log
            RecordRequest auditLog = convertToGrpcRequest(payload, "Status updated to Sent for protocol conversion", recordId);
            grpcClient.saveRecord(auditLog);
            log.info("🧾 Added Change Request Log entry for Record ID: {}", recordId);

        } catch (Exception e) {
            log.error("❌ Error processing request for Sensor ID {} (Record ID {}): {}",
                    payload.getSensorId(), recordId != null ? recordId : "N/A", e.getMessage(), e);
        }
    }

    private RecordRequest convertToGrpcRequest(RequestPayload payload, String status, String recordId) {
        RecordRequest.Builder builder = RecordRequest.newBuilder()
                .setSensorId(payload.getSensorId())
                .setOperation(payload.getOperation())
                .setRelayNumber(payload.getRelayNumber())
                .setDuration(payload.getDuration())
                .setStatus(status);

        if (recordId != null && !recordId.isEmpty()) {
            builder.setRecordId(recordId);
        }

        return builder.build();
    }
}
