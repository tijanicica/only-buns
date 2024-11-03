package com.project.onlybuns.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationEmail(String email, String token) {
        String activationLink = "http://localhost:8080/api/users/activate?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OnlyBuns Account Activation");
        message.setText("Welcome to OnlyBuns! Please activate your account by clicking the link: " + activationLink);

        try {
            mailSender.send(message);
            System.out.println("Activation email sent to " + email);
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());

        }
    }
}
