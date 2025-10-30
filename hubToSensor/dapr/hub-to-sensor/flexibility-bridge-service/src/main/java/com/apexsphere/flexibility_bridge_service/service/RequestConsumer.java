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
            // 1️⃣ Save initial record
            RecordRequest saveRequest = convertToGrpcRequest(payload, "Control Requested", null);
            recordId = grpcClient.saveRecord(saveRequest);
            log.info("➡️ Saved record to DB. Status: Control Requested. Record ID: {}", recordId);

            // 2️⃣ Publish to connector.request (handled inside producerService)
            producerService.sendRequestToConnector(payload, recordId);

            // 3️⃣ Update record after publishing
            RecordRequest updateRequest = convertToGrpcRequest(payload, "Sent for protocol conversion", recordId);
            grpcClient.updateRecordStatus(updateRequest);
            log.info("📢 Updated record status to 'Sent for protocol conversion'. Record ID: {}", recordId);

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
