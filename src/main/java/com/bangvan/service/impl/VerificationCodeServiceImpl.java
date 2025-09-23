package com.bangvan.service.impl;

import com.bangvan.dto.request.verify.VerificationRequest;
import com.bangvan.entity.User;
import com.bangvan.entity.VerificationCode;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.UserRepository;
import com.bangvan.repository.VerificationCodeRepository;
import com.bangvan.service.EmailService;
import com.bangvan.service.VerificationCodeService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Async
    @Override
    public void sendVerificationOtpEmail(String userEmail) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("email", "email", userEmail));
        try {
            if (verificationCode == null || verificationCode.getExpiredTime().isBefore(LocalDateTime.now())) {
                String otp = generateVerificationCode();
                verificationCode = new VerificationCode();
                verificationCode.setOtp(otp);
                verificationCode.setEmail(userEmail);
                verificationCode.setExpiredTime(LocalDateTime.now().plusMinutes(15));
                verificationCodeRepository.save(verificationCode);
                emailService.sendVerificationOtpEmail(userEmail, otp);

            } else {
                emailService.sendVerificationOtpEmail(userEmail, verificationCode.getOtp());
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String verifyOtp(VerificationRequest request) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("email", "email", request.getEmail()));
        if (verificationCode == null || !verificationCode.getOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }
        if (verificationCode.getExpiredTime().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }
        User user = verificationCode.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        verificationCodeRepository.save(verificationCode);
        return "User verified successfully.";
    }

    @Override
    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

}
