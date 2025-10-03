package com.apexsphere.protocol_adapter_service.service;

import com.apexsphere.protocol_adapter_service.model.FlexibilityResponse;
import com.apexsphere.protocol_adapter_service.model.RequestPayload;
import org.springframework.stereotype.Component;

/**
 * Handles the conversion between the internal JSON format (RequestPayload) 
 * and the external XML format (FlexibilityResponse/HesRequest).
 * Uses JAXB for XML marshalling/unmarshalling.
 */
@Component
public class ProtocolConverter {

    // The simpleMessageConverter @Bean method has been removed as it is now redundant, 
    // given the MarshallingMessageConverter is correctly configured in RabbitMQConfig.

    /**
     * Converts the JSON request payload object into an XML string format.
     * @param payload The incoming JSON-based RequestPayload.
     * @return A String representing the XML version of the request.
     */
    public String convertPayloadToXml(RequestPayload payload) {
        // --- PLACEHOLDER LOGIC (Kept from previous version) ---
        // TODO: Implement actual JSON-to-XML conversion logic here, 
        // using libraries like Jackson XML, JAXB, or similar.

        // For now, we return a simple XML structure for demonstration:
        return String.format(
            "<HesRequest id=\"%s\">" +
            "<operation>%s</operation>" +
            "<relay>%d</relay>" +
            "<duration>%d</duration>" +
            "</HesRequest>",
            payload.getSensorId(),
            payload.getOperation(),
            payload.getRelayNumber(),
            payload.getDuration()
        );
    }

    /**
     * Converts an unmarshalled FlexibilityResponse object (received from HES) 
     * back into a JSON string suitable for publishing to the Bridge.
     * * NOTE: The XML->Object unmarshalling is now handled by the xmlListenerContainerFactory.
     * This method focuses on the Object->JSON step.
     * * @param response The FlexibilityResponse object.
     * @return A String representing the JSON version of the response.
     */
    public String convertResponseToJson(FlexibilityResponse response) {
        try {
            // 1. Convert the Java Object (FlexibilityResponse) into a JSON String
            
            // --- PLACEHOLDER LOGIC ---
            // In a real application, you would use an ObjectMapper from Jackson or Gson here:
            // ObjectMapper mapper = new ObjectMapper();
            // String jsonString = mapper.writeValueAsString(response);
            
            // For now, we manually create a simple JSON representation from the object's properties:
            return String.format(
                "{\"requestId\":\"%s\", \"status\":\"%s\", \"message\":\"%s\", \"errorCode\":\"%s\"}",
                response.getRequestId(),
                response.getStatus(),
                response.getMessage(),
                // Include ErrorCode only if present
                response.getErrorCode() != null ? response.getErrorCode() : "" 
            );

        } catch (Exception e) {
            // In case of JSON mapping failure
            throw new RuntimeException("Protocol conversion (FlexibilityResponse to JSON) failed: " + e.getMessage(), e);
        }
    }
}
