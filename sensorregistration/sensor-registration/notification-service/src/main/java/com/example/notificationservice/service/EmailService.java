package com.example.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    public void sendRegistrationConfirmationEmail(String toEmail, String sensorId) {
        LOGGER.info("Simulating sending registration confirmation email to: {} for sensor ID: {}", toEmail, sensorId);
        //  No actual email sending here
    }
}

/* package com.example.notificationservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationConfirmationEmail(String toEmail, String sensorId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Sensor Registration Confirmation");
        message.setText("Dear User,\n\nYour sensor with ID: " + sensorId + " has been successfully registered.\n\nThank you!");

        mailSender.send(message);
    }
} */