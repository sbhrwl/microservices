// hub-service/src/main/java/com/example/hubservice/service/RegistrationServiceImpl.java
package com.example.hubservice.service;

import com.example.hubservice.proto.RegistrationRequestMessage;
import com.example.hubservice.proto.RegistrationResponseMessage;
import com.example.hubservice.proto.RegistrationServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service; // Use @Service for Spring component scanning

/**
 * RegistrationServiceImpl is the concrete implementation of the gRPC RegistrationService.
 * This class handles incoming RegisterSensor RPC calls from clients (like the Ingestion Service).
 * It extends the generated base class and overrides the service methods.
 */
@Service // Mark as a Spring service so it can be managed by the Spring context
public class RegistrationServiceImpl extends RegistrationServiceGrpc.RegistrationServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    /**
     * Implements the unary RegisterSensor RPC method.
     * This method receives a RegistrationRequestMessage and sends back a RegistrationResponseMessage.
     *
     * @param request The incoming RegistrationRequestMessage from the client.
     * @param responseObserver A StreamObserver to send the response back to the client.
     */
    @Override
    public void registerSensor(RegistrationRequestMessage request, StreamObserver<RegistrationResponseMessage> responseObserver) {
        logger.info("Received gRPC RegistrationRequestMessage: SensorId={}, SensorModel={}, Email={}",
                    request.getSensorId(), request.getSensorModel(), request.getEmail());

        // --- Business Logic Placeholder ---
        // In a real application, you would perform actions here like:
        // - Validating the input data
        // - Storing sensor information in a database
        // - Communicating with other services
        // - Generating an actual registration token or ID

        boolean success = true;
        String message = "Sensor registered successfully.";

        // Example: Basic validation
        if (request.getSensorId().isEmpty() || request.getSensorModel().isEmpty()) {
            success = false;
            message = "Sensor ID and Sensor Model cannot be empty.";
            logger.warn("Sensor registration failed due to invalid input: {}", message);
        } else {
            logger.info("Processing registration for sensor: {}", request.getSensorId());
            // Simulate some processing time or database interaction
            try {
                Thread.sleep(100); // Simulate work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted during simulated processing.", e);
                success = false;
                message = "Internal server error during processing.";
            }
        }

        // Build the response message
        RegistrationResponseMessage response = RegistrationResponseMessage.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .build();

        logger.info("Sending gRPC RegistrationResponseMessage: Success={}, Message='{}'",
                    response.getSuccess(), response.getMessage());

        // Send the response back to the client
        responseObserver.onNext(response);
        // Mark the RPC call as complete
        responseObserver.onCompleted();
    }
}
