package com.bangvan.service;

import com.bangvan.dto.request.verify.VerificationRequest;
import org.springframework.scheduling.annotation.Async;

public interface VerificationCodeService {
    @Async
    void sendVerificationOtpEmail(String userEmail);

    String verifyOtp(VerificationRequest request);

    String generateVerificationCode();


}
