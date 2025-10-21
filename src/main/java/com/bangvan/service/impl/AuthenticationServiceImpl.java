package com.bangvan.service.impl;

import com.bangvan.dto.request.auth.LoginRequest;
import com.bangvan.dto.request.auth.RefreshTokenRequest;
import com.bangvan.dto.request.auth.RegisterRequest;
import com.bangvan.dto.response.auth.TokenResponse;

import com.bangvan.dto.response.user.UserResponse;
import com.bangvan.entity.*;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;

import com.bangvan.repository.CartRepository;
import com.bangvan.repository.RoleRepository;
import com.bangvan.repository.UserRepository;
//import com.bangvan.repository.VerificationCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import com.bangvan.service.AuthenticationService;
import com.bangvan.service.JwtService;
import com.bangvan.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
//    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeService verificationCodeService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.jwt.refresh-expiration-milliseconds}")
    private Long refreshTokenExpirationMilliseconds;

    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TokenResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        }catch (DisabledException e){
            throw new AppException(ErrorCode.USER_NOT_VERIFIED);
        }catch (BadCredentialsException e){
            throw new AppException(ErrorCode.WRONG_USERNAME_OR_PASSWORD);
        }catch (Exception e){
            log.error("Authentication failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
        }

        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String redisKey = REFRESH_TOKEN_PREFIX + user.getUsername();
        redisTemplate.opsForValue().set(redisKey, refreshToken, refreshTokenExpirationMilliseconds, TimeUnit.MILLISECONDS);
        log.info("Refresh token for user {} stored in Redis with TTL {} ms", user.getUsername(), refreshTokenExpirationMilliseconds);

        long now = System.currentTimeMillis();

        return TokenResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .expiredTime(new Timestamp(now + jwtService.getExpirationTime()))
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }


        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND))));

        user.setEnabled(false);

        user = userRepository.save(user);
        log.info("User {} saved with enabled=false", user.getUsername());

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
        log.info("Cart created for user {}", user.getUsername());


//        String otp = verificationCodeService.generateVerificationCode();
//        VerificationCode verificationCode = new VerificationCode();
//        verificationCode.setOtp(otp);
//        verificationCode.setEmail(request.getEmail());
//        verificationCode.setExpiredTime(LocalDateTime.now().plusMinutes(15));
//        verificationCode.setUser(user);
        verificationCodeService.generateAndSendVerificationOtp(user);
        log.info("OTP sent to email {}", request.getEmail());

//        verificationCodeRepository.save(verificationCode);
//        verificationCodeService.sendVerificationOtpEmail(request.getEmail());

        log.info("Saving user to database");
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest refreshToken) {
        if (jwtService.isTokenExpired(refreshToken.getRefreshToken())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        String username;
        try {
            username = jwtService.extractUsername(refreshToken.getRefreshToken());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "Invalid Refresh Token signature");
        }
        UserDetails userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String redisKey = REFRESH_TOKEN_PREFIX + username;
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);
        if (storedToken == null) {
            log.warn("Refresh token not found in Redis for user {}", username);
            throw new AppException(ErrorCode.TOKEN_EXPIRED, "Refresh token has expired or is invalid (not found in store)");
        }
        if (!storedToken.equals(refreshToken.getRefreshToken())) {
            log.warn("Provided refresh token does not match stored token for user {}", username);
            // Có thể thực hiện thu hồi tất cả token của user ở đây nếu cần bảo mật cao hơn
            redisTemplate.delete(redisKey); // Xóa token cũ (không hợp lệ) khỏi Redis
            throw new AppException(ErrorCode.INVALID_TOKEN, "Refresh token mismatch (possible reuse attempt)");
        }
        // 4. Kiểm tra token có hợp lệ với userDetails không (thực ra bước 3 đã đảm bảo phần nào)
        if (!jwtService.isTokenValid(refreshToken.getRefreshToken(), userDetails)) {
            // Trường hợp này ít xảy ra nếu check Redis thành công, nhưng để đảm bảo
            redisTemplate.delete(redisKey); // Xóa token cũ (không hợp lệ) khỏi Redis
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

                String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, refreshTokenExpirationMilliseconds, TimeUnit.MILLISECONDS);
        log.info("Rotated refresh token for user {} stored in Redis", username);

        long now = System.currentTimeMillis();
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiredTime(new Timestamp(now + jwtService.getExpirationTime()))
                .build();
    }
    @Transactional
    public void logout(String username) {
        String redisKey = REFRESH_TOKEN_PREFIX + username;
        Boolean deleted = redisTemplate.delete(redisKey);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("Refresh token for user {} deleted from Redis (logout).", username);
        } else {
            log.warn("Attempted to logout user {}, but no refresh token found in Redis.", username);
        }
        // Nếu bạn implement blacklist access token, hãy thêm logic blacklist ở đây
    }


}
