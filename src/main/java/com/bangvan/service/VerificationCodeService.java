package com.bangvan.service;

import com.bangvan.dto.request.verify.VerificationRequest;
import com.bangvan.entity.User;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface VerificationCodeService {
    @Async
    void sendVerificationOtpEmail(String userEmail, String otp) throws MessagingException;
    @Async
    void generateAndSendVerificationOtp(User user);

    String verifyOtp(VerificationRequest request);

    String generateVerificationCode();


}
