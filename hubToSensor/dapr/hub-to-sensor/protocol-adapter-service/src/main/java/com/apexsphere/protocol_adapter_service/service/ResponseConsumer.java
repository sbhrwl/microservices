package com.apexsphere.protocol_adapter_service.service;

import com.apexsphere.protocol_adapter_service.model.FlexibilityResponse;
import com.apexsphere.storage_service.service.RecordRequest; 
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Consumer service for receiving XML responses from the HES/Connector service.
 * It is configured to use the 'xmlListenerContainerFactory' to unmarshal the 
 * XML into a FlexibilityResponse object directly, then converts it to JSON 
 * for publishing to the Bridge/Hub.
 */
@Component
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

    // IMPORTANT: Using 'xmlListenerContainerFactory' from RabbitMQConfig.java.
    // This factory uses MarshallingMessageConverter to convert XML directly to FlexibilityResponse object.
    @RabbitListener(
        queues = "${messaging.rabbitmq.response-inbound-queue}",
        containerFactory = "xmlListenerContainerFactory" 
    )
    // The method now receives the fully unmarshalled Java object, resolving the conversion failure.
    public void handleResponse(FlexibilityResponse response) {

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
