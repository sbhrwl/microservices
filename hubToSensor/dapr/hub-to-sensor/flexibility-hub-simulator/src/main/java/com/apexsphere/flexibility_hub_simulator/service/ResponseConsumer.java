package com.apexsphere.flexibility_hub_simulator.service;

import com.apexsphere.flexibility_hub_simulator.model.MessageResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumer service in the Hub Simulator responsible for receiving the final 
 * JSON response from the Bridge service and logging the outcome.
 */
@Service
public class ResponseConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(ResponseConsumer.class);

    /**
     * Listens for the final response on the flexibility-hub.response queue.
     * The message converter automatically deserializes the JSON payload into a MessageResponse object.
     * @param response The deserialized final response object.
     */
    @RabbitListener(queues = "${messaging.rabbitmq.response-queue}")
    public void handleFinalResponse(MessageResponse response) {
        
        log.info("=========================================================================================");
        log.info("✅ FINAL RESPONSE RECEIVED from Bridge for Request ID: {}", response.getRequestId());
        log.info("   Status: {}", response.getStatus());
        log.info("   Message: {}", response.getMessage());
        
        if (response.getErrorCode() != null) {
            log.error("   Error Code: {}", response.getErrorCode());
        }
        
        // Print the full JSON representation to the console for complete verification
        log.info("   RAW RESPONSE JSON: {}", response.toString()); 
        log.info("=========================================================================================");
        
        // In a real application, you would update a database record, notify a user, etc., here.
    }
}
