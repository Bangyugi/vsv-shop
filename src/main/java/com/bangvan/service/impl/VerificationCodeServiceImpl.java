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
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> stringRedisTemplate;

    @Value("${app.otp.expiration-minutes:5}")
    private int otpExpirationMinutes;

    private static final String OTP_PREFIX = "otp:";

    @Async
    @Override
    public void sendVerificationOtpEmail(String userEmail, String otp) throws MessagingException {
        try {
            log.info("Attempting to send OTP {} to email {}", otp, userEmail);
            emailService.sendVerificationOtpEmail(userEmail, otp);
            log.info("Successfully sent OTP to email {}", userEmail);
        } catch (MessagingException e) {
            log.error("MessagingException while sending OTP to {}: {}", userEmail, e.getMessage());
            throw new AppException(ErrorCode.EMAIL_REQUEST_FAILED, "Failed to prepare email sending process."); // Ném lỗi cụ thể hơn
        } catch (Exception e) { // Bắt các lỗi khác có thể xảy ra khi gửi mail
            log.error("Failed to send OTP email to {}: {}", userEmail, e.getMessage());
            throw new AppException(ErrorCode.EMAIL_SEND_FAILED, "Failed to send OTP email.");
        }
    }

    @Async
    @Override
    @Transactional
    public void generateAndSendVerificationOtp(User user) {
        if (user == null || user.getEmail() == null) {
            log.error("Cannot send OTP: User or user email is null.");
            return;
        }
        String userEmail = user.getEmail();
        String redisKey = OTP_PREFIX + userEmail;

        String otp = generateVerificationCode();
        try {

            stringRedisTemplate.opsForValue().set(redisKey, otp, Duration.ofMinutes(otpExpirationMinutes));
            log.info("Stored OTP for {} in Redis with TTL {} minutes.", userEmail, otpExpirationMinutes);
            sendVerificationOtpEmail(userEmail, otp);

        } catch (Exception e) {
            log.error("Error during OTP generation/sending for email {}: {}", userEmail, e.getMessage());
             stringRedisTemplate.delete(redisKey);
        }
    }

    @Override
    @Transactional
    public String verifyOtp(VerificationRequest request) {
        String email = request.getEmail();
        String providedOtp = request.getOtp();
        String redisKey = OTP_PREFIX + email;

        log.info("Verifying OTP for email: {}", email);

        String storedOtp = stringRedisTemplate.opsForValue().get(redisKey);

        if (storedOtp == null) {
            log.warn("OTP not found or expired in Redis for email: {}", email);
            throw new AppException(ErrorCode.EXPIRED_OTP);
        }

        if (!storedOtp.equals(providedOtp)) {
            log.warn("Invalid OTP provided for email: {}", email);
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        user.setEnabled(true);
        userRepository.save(user);
        log.info("User {} enabled successfully.", user.getUsername());
        stringRedisTemplate.delete(redisKey);
        log.info("OTP for email {} deleted from Redis.", email);

        return "User verified successfully.";
    }

    @Override
    public String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

}
