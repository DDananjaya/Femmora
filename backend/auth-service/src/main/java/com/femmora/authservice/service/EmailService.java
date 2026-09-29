package com.femmora.authservice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String toEmail, String resetCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("femmoraclothinglk@gmail.com");
        message.setTo(toEmail);
        message.setSubject("FEMMORA / Password Reset Verification Code");
        message.setText("Your verification code to reset your Femmora password is: " + resetCode + "\n\nThis code is valid for single use.");

        mailSender.send(message);
    }

    public void sendEmailVerificationCode(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("femmoraclothinglk@gmail.com");
        message.setTo(toEmail);
        message.setSubject("FEMMORA / Email Address Change Verification");
        message.setText("Your verification code to update your Femmora email address is: " + verificationCode + "\n\nIf you did not request this, please ignore this email.");

        mailSender.send(message);
    }
}