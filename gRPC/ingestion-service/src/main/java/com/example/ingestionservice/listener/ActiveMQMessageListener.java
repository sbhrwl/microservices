package com.example.ingestionservice.listener;

import com.example.ingestionservice.model.RegistrationRequestPojo;
import com.example.ingestionservice.proto.RegistrationRequestMessage;
import com.example.ingestionservice.proto.RegistrationResponseMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * ActiveMQMessageListener is a Spring component responsible for listening to
 * messages from the configured ActiveMQ queue, deserializing them into a POJO,
 * and then mapping that POJO to a Protobuf message.
 */
@Component
public class ActiveMQMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQMessageListener.class);

    /**
     * This method listens for messages on the 'registration.queue'.
     * Spring's JMS auto-configuration, combined with Jackson (if on classpath),
     * will automatically attempt to deserialize the incoming JSON string into
     * the RegistrationRequestPojo object.
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

        // TODO: In the next step, this is where we will add the gRPC client call
        // to send 'protobufMessage' to the Hub Service.
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