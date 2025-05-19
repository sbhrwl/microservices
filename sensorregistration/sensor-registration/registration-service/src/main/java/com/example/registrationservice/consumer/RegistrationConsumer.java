package com.example.registrationservice.consumer;

import com.example.registrationservice.document.Registration;
import com.example.registrationservice.dto.RegistrationDTO;
import com.example.registrationservice.repository.RegistrationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RegistrationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationConsumer.class);

    @Autowired
    private RegistrationRepository RegistrationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topic.sensor-registration}", groupId = "registration-service-group")
    public void consumeRegistration(ConsumerRecord<String, String> record) {
        try {
            RegistrationDTO registrationDTO = objectMapper.readValue(record.value(), RegistrationDTO.class);
            Registration registration = new Registration();
            registration.setSensorId(registrationDTO.getSensorId());
            registration.setSensorModel(registrationDTO.getSensorModel());
            registration.setEmail(registrationDTO.getEmail());

            RegistrationRepository.save(registration);
            LOGGER.info("Successfully saved registration for sensor ID: {}", registration.getSensorId());
        } catch (Exception e) {
            LOGGER.error("Error processing Kafka message: {}", e.getMessage());
        }
    }
}