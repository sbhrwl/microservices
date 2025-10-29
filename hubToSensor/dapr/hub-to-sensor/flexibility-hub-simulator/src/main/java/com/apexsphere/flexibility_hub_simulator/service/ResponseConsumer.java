package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

/**
 * Consumer service in the Hub Simulator responsible for receiving the final 
 * JSON response from the Bridge service and logging the outcome via Dapr.
 */
@Controller
public class ResponseConsumer {

    private static final Logger log = LoggerFactory.getLogger(ResponseConsumer.class);

    @Value("${messaging.dapr.pubsub-name}")
    private String pubsubName;

    @Value("${messaging.dapr.response-topic}")
    private String responseTopic;

    /**
     * Dapr subscription endpoint.
     * Dapr calls this to know which topics this service subscribes to.
     */
    @GetMapping("/dapr/subscribe")
    @ResponseBody
    public List<Map<String, String>> subscribe() {
        return List.of(Map.of(
                "pubsubname", pubsubName,
                "topic", responseTopic,
                "route", "handle-final-response"
        ));
    }

    /**
     * Handles the final response message sent by Dapr pub/sub.
     */
    @PostMapping(path = "/handle-final-response")
    @ResponseBody
    public void handleFinalResponse(@RequestBody MessageResponse response) {

        log.info("=========================================================================================");
        log.info("✅ FINAL RESPONSE RECEIVED from Bridge for Request ID: {}", response.getRequestId());
        log.info("   Status: {}", response.getStatus());
        log.info("   Message: {}", response.getMessage());

        if (response.getErrorCode() != null) {
            log.error("   Error Code: {}", response.getErrorCode());
        }

        log.info("   RAW RESPONSE JSON: {}", response.toString());
        log.info("=========================================================================================");

        // Optional: update DB, notify user, etc.
    }
}