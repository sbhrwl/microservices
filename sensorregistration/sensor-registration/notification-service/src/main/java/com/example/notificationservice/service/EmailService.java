package com.example.notificationservice.service;

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
}