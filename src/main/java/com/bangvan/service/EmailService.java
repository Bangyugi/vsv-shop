package com.bangvan.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendVerificationOtpEmail(String userEmail, String otp) throws MessagingException;
}
