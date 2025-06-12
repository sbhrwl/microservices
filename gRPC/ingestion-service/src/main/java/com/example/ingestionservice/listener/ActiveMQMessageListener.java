// src/main/java/com/example/ingestionservice/listener/ActiveMQMessageListener.java
package com.example.ingestionservice.listener;

import com.example.ingestionservice.model.RegistrationRequestPojo;
import com.example.ingestionservice.proto.RegistrationRequestMessage;
import com.example.ingestionservice.proto.RegistrationResponseMessage;
import com.example.ingestionservice.proto.RegistrationServiceGrpc; // Keep this import
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * ActiveMQMessageListener is a Spring component responsible for listening to
 * messages from the configured ActiveMQ queue, deserializing them into a POJO,
 * mapping that POJO to a Protobuf message, and then sending it via gRPC.
 */
@Component
public class ActiveMQMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQMessageListener.class);

    // Uncomment this line to autowire the gRPC blocking stub
    @Autowired
    private RegistrationServiceGrpc.RegistrationServiceBlockingStub registrationServiceBlockingStub;

    /**
     * This method listens for messages on the 'registration.queue'.
     * Spring's JMS auto-configuration will deserialize the incoming JSON string
     * into the RegistrationRequestPojo object.
     *
     * @param registrationRequestPojo The deserialized POJO representing the incoming message.
     */
    @JmsListener(destination = "${spring.activemq.queue-name}")
    public void receiveMessage(RegistrationRequestPojo registrationRequestPojo) {
        logger.info("Received message from ActiveMQ: {}", registrationRequestPojo);

        // --- Data Mapping: Convert POJO to Protobuf message ---
        RegistrationRequestMessage protobufMessage = mapToProtobuf(registrationRequestPojo);

        logger.info("Mapped to Protobuf message: sensorId={}, sensorModel={}, email={}",
                    protobufMessage.getSensorId(),
                    protobufMessage.getSensorModel(),
                    protobufMessage.getEmail());

        // --- gRPC Client Call: This block is now re-enabled ---
        try {
            logger.info("Sending gRPC request to Hub Service for sensor: {}", protobufMessage.getSensorId());
            // Make the gRPC call using the injected blocking stub
            RegistrationResponseMessage response = registrationServiceBlockingStub.registerSensor(protobufMessage);
            logger.info("Received gRPC response from Hub Service: Success={}, Message='{}'",
                        response.getSuccess(), response.getMessage());

            if (!response.getSuccess()) {
                logger.warn("Sensor registration failed for {}: {}", protobufMessage.getSensorId(), response.getMessage());
                // TODO: Implement more robust error handling / retry logic here
            }
        } catch (Exception e) {
            logger.error("gRPC call to Hub Service failed for sensor {}: {}", protobufMessage.getSensorId(), e.getMessage(), e);
            // TODO: Implement more robust error handling (e.g., dead-letter queue, metrics)
        }
    }

    /**
     * Maps a RegistrationRequestPojo object to a RegistrationRequestMessage Protobuf object.
     * This method uses the Protobuf builder pattern for creation.
     *
     * @param pojo The POJO to be mapped.
     * @return A new RegistrationRequestMessage Protobuf object.
     */
    private RegistrationRequestMessage mapToProtobuf(RegistrationRequestPojo pojo) {
        return RegistrationRequestMessage.newBuilder()
                .setSensorId(pojo.getSensorId())
                .setSensorModel(pojo.getSensorModel())
                .setEmail(pojo.getEmail())
                .build();
    }
}
