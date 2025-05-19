package com.example.registrationservice.consumer;

import com.example.registrationservice.document.Registration;
import com.example.registrationservice.dto.RegistrationDTO;
import com.example.registrationservice.repository.RegistrationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RegistrationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationConsumer.class);

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    @KafkaListener(topics = "${kafka.topic.sensor-registration}", groupId = "registration-service-group")
    public void consumeRegistration(ConsumerRecord<String, String> record) {
        try {
            RegistrationDTO registrationDTO = objectMapper.readValue(record.value(), RegistrationDTO.class);
            Registration registration = new Registration();
            registration.setSensorId(registrationDTO.getSensorId());
            registration.setSensorModel(registrationDTO.getSensorModel());
            registration.setEmail(registrationDTO.getEmail());

            registrationRepository.save(registration);
            LOGGER.info("Successfully saved registration for sensor ID: {}", registration.getSensorId());

            // Call notification service
            String notificationUrl = notificationServiceUrl + "/api/notification/email/registration-confirmation?toEmail=" + registrationDTO.getEmail() + "&sensorId=" + registrationDTO.getSensorId();
            ResponseEntity<String> response = restTemplate.postForEntity(notificationUrl, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Successfully sent notification email for sensor ID: {}", registration.getSensorId());
            } else {
                LOGGER.error("Failed to send notification email for sensor ID: {}.  Status code: {}", registration.getSensorId(), response.getStatusCode());
            }

        } catch (Exception e) {
            LOGGER.error("Error processing Kafka message: {}", e.getMessage());
        }
    }
}