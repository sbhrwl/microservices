package com.apexsphere.protocol_adapter_service.service;

import com.apexsphere.protocol_adapter_service.model.FlexibilityResponse;
import com.apexsphere.storage_service.service.RecordRequest; 
import io.dapr.Topic;
import io.dapr.client.domain.CloudEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Consumer service for receiving XML responses from the HES/Connector service.
 * It is configured to use the 'xmlListenerContainerFactory' to unmarshal the 
 * XML into a FlexibilityResponse object directly, then converts it to JSON 
 * for publishing to the Bridge/Hub.
 */
@RestController
public class ResponseConsumer {

    private static final Logger log = LoggerFactory.getLogger(ResponseConsumer.class);

    // Dependencies
    private final RecordGrpcClient grpcClient; 
    private final ResponseProducerToBridge producerToBridge;
    private final ProtocolConverter protocolConverter;

    @Autowired
    public ResponseConsumer(
            RecordGrpcClient grpcClient, 
            ResponseProducerToBridge producerToBridge,
            ProtocolConverter protocolConverter) { 
        this.grpcClient = grpcClient;
        this.producerToBridge = producerToBridge;
        this.protocolConverter = protocolConverter;
    }

    @Topic(name = "${messaging.dapr.hes-response-topic}", pubsubName = "${messaging.dapr.pubsub-name}")
    @PostMapping(path = "/hes.response")
    public void handleResponse(@RequestBody(required = false) CloudEvent<String> cloudEvent) {
        if (cloudEvent == null || cloudEvent.getData() == null) {
            log.warn("⚠️ Received empty CloudEvent data — ignoring message.");
            return;
        }

        String xmlPayload = cloudEvent.getData();
        FlexibilityResponse response = protocolConverter.parseXmlToFlexibilityResponse(xmlPayload);

        String recordId = response.getRequestId();
        log.info("✅ Received object response for RequestID: {}", recordId);
        log.debug("Full Response Object: {}", response.toString());

        String internalStatus;
        
        try {
            // 1. UPDATE initial status to "Response recieved from HES"
            RecordRequest initialUpdateRequest = convertToGrpcRequest(recordId, "Response recieved from HES");
            grpcClient.updateRecordStatus(initialUpdateRequest);
            log.info("➡️ Updated status to 'Response recieved from HES' for ID: {}", recordId);
            
            // 2. CONVERT FlexibilityResponse Object to JSON String
            log.info("🔄 Starting protocol conversion (Object to JSON String) for Record ID: {}", recordId);
            
            // Call the updated method in ProtocolConverter
            String jsonPayload = protocolConverter.convertResponseToJson(response);

            // 3. UPDATE status in DB as "Protocol conversion done for response"
            RecordRequest conversionDoneUpdate = convertToGrpcRequest(recordId, "Protocol conversion done for response");
            grpcClient.updateRecordStatus(conversionDoneUpdate);
            log.info("📝 Updated status to 'Protocol conversion done for response' for ID: {}", recordId);
            log.debug("Converted JSON payload snippet: {}", jsonPayload.substring(0, Math.min(jsonPayload.length(), 100)));


            // 4. PUBLISH JSON Response to Bridge queue
            producerToBridge.sendResponseToHub(jsonPayload, recordId);

            // 5. Determine Final Status and UPDATE status in DB as "Response sent to Bridge"
            if ("SUCCESS".equalsIgnoreCase(response.getStatus())) {
                internalStatus = "COMPLETED"; // Final status if successful
            } else {
                 // Use the error details from the XML response object
                 internalStatus = String.format("FAILED (Code: %s, Msg: %s)", 
                    response.getErrorCode(), response.getMessage());
            }

            RecordRequest sentToBridgeUpdate = convertToGrpcRequest(recordId, "Response sent to Bridge - Final Status: " + internalStatus);
            String updateResponse = grpcClient.updateRecordStatus(sentToBridgeUpdate);

            log.info("📢 Updated status to 'Response sent to Bridge' for ID: {}. Final status: {}", recordId, internalStatus);
            log.debug("✅ gRPC update successful. Server message: {}", updateResponse);

        } catch (Exception e) {
            log.error("❌ Fatal error in ResponseConsumer for Record ID {}: {}", recordId, e.getMessage(), e);
            // Attempt to update status to indicate failure in protocol adapter if ID is known
            try {
                RecordRequest failureUpdate = convertToGrpcRequest(recordId, "PROTOCOL_ADAPTER_ERROR: " + e.getMessage());
                grpcClient.updateRecordStatus(failureUpdate);
            } catch (Exception grpcE) {
                log.error("Failed to report fatal error via gRPC for ID {}: {}", recordId, grpcE.getMessage());
            }
        }
    }
    
    /**
     * Converts the response data into the gRPC RecordRequest message for updates.
     * @param recordId The unique ID of the record to update.
     * @param status The new status string.
     * @return A RecordRequest object ready for gRPC consumption.
     */
    private RecordRequest convertToGrpcRequest(String recordId, String status) {
        RecordRequest.Builder builder = RecordRequest.newBuilder()
                .setRecordId(recordId) 
                .setStatus(status);
        
        return builder.build();
    }
}
