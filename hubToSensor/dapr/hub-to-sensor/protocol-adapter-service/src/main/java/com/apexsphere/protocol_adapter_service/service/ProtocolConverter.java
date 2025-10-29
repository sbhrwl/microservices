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

    /**
     * Converts the JSON request payload object into an XML string format.
     * * IMPORTANT FIX: Injects the <RequestID> tag using the Record ID from the payload.
     * * @param payload The incoming JSON-based RequestPayload.
     * @return A String representing the XML version of the request.
     */
    public String convertPayloadToXml(RequestPayload payload) {
        // We now return the XML structure including the mandatory <RequestID> tag
        // which the HES simulator expects to echo in its response.
        return String.format(
            "<HesRequest id=\"%s\">" +
            "    <RequestID>%s</RequestID>" + // <-- REQUIRED HES REQUEST ID TAG ADDED
            "    <operation>%s</operation>" +
            "    <relay>%d</relay>" +
            "    <duration>%d</duration>" +
            "</HesRequest>",
            payload.getSensorId(),
            payload.getRecordId(), // <-- Using the recordId from the payload
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

    /**
     * Parses a minimal XML response string from HES into a FlexibilityResponse object.
     * Assumes a simple structure containing RequestID, status, message, and optional errorCode.
     */
    public FlexibilityResponse parseXmlToFlexibilityResponse(String xml) {
        if (xml == null || xml.isBlank()) {
            throw new IllegalArgumentException("XML payload is empty");
        }

        String requestId = extract(xml, "RequestID");
        String status = extract(xml, "status");
        String message = extract(xml, "message");
        String errorCode = extract(xml, "errorCode");

        FlexibilityResponse response = new FlexibilityResponse();
        response.setRequestId(requestId);
        response.setStatus(status);
        response.setMessage(message);
        response.setErrorCode(errorCode != null && !errorCode.isBlank() ? errorCode : null);
        return response;
    }

    private String extract(String xml, String tag) {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open);
        int end = xml.indexOf(close);
        if (start == -1 || end == -1 || end <= start) {
            return null;
        }
        start += open.length();
        return xml.substring(start, end).trim();
    }
}
