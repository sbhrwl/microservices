// hub-service/src/main/java/com/example/hubservice/service/RegistrationServiceImpl.java
package com.example.hubservice.service;

import com.example.hubservice.entity.SensorRegistration; // Import the entity
import com.example.hubservice.proto.RegistrationRequestMessage;
import com.example.hubservice.proto.RegistrationResponseMessage;
import com.example.hubservice.proto.RegistrationServiceGrpc;
import com.example.hubservice.repository.SensorRegistrationRepository; // Import the repository
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime; // Import for timestamp

/**
 * RegistrationServiceImpl is the concrete implementation of the gRPC RegistrationService.
 * This class handles incoming RegisterSensor RPC calls from clients (like the Ingestion Service).
 * It now integrates with a PostgreSQL database to persist sensor registration data.
 */
@Service
public class RegistrationServiceImpl extends RegistrationServiceGrpc.RegistrationServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    // Autowire the SensorRegistrationRepository
    @Autowired
    private SensorRegistrationRepository sensorRegistrationRepository;

    /**
     * Implements the unary RegisterSensor RPC method.
     * This method receives a RegistrationRequestMessage and sends back a RegistrationResponseMessage.
     * It now attempts to save the sensor registration details to the database.
     *
     * @param request The incoming RegistrationRequestMessage from the client.
     * @param responseObserver A StreamObserver to send the response back to the client.
     */
    @Override
    public void registerSensor(RegistrationRequestMessage request, StreamObserver<RegistrationResponseMessage> responseObserver) {
        logger.info("Received gRPC RegistrationRequestMessage: SensorId={}, SensorModel={}, Email={}",
                    request.getSensorId(), request.getSensorModel(), request.getEmail());

        boolean success = true;
        String message = "Sensor registered successfully.";

        // Basic validation: Check if sensorId and sensorModel are not empty
        if (request.getSensorId().isEmpty() || request.getSensorModel().isEmpty()) {
            success = false;
            message = "Sensor ID and Sensor Model cannot be empty.";
            logger.warn("Sensor registration failed due to invalid input: {}", message);
        } else {
            try {
                // Create a SensorRegistration entity from the Protobuf request message
                SensorRegistration newRegistration = new SensorRegistration(
                        request.getSensorId(),
                        request.getSensorModel(),
                        request.getEmail(),
                        LocalDateTime.now() // Set current timestamp
                );

                // Save the entity to the database using the repository
                SensorRegistration savedRegistration = sensorRegistrationRepository.save(newRegistration);
                logger.info("Sensor registration saved to database: ID={}, SensorId={}",
                            savedRegistration.getId(), savedRegistration.getSensorId());

            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                // Handle cases where sensorId might already exist (due to unique=true constraint)
                success = false;
                message = "Sensor with ID '" + request.getSensorId() + "' already registered.";
                logger.warn("Data integrity violation: Sensor registration failed for {}: {}", request.getSensorId(), e.getMessage());
            } catch (Exception e) {
                success = false;
                message = "Failed to save sensor registration due to internal error.";
                logger.error("Error saving sensor registration for {}: {}", request.getSensorId(), e.getMessage(), e);
            }
        }

        // Build and send the response message
        RegistrationResponseMessage response = RegistrationResponseMessage.newBuilder()
                .setSuccess(success)
                .setMessage(message)
                .build();

        logger.info("Sending gRPC RegistrationResponseMessage: Success={}, Message='{}'",
                    response.getSuccess(), response.getMessage());

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
