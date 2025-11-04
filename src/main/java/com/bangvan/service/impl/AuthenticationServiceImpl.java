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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import com.bangvan.service.AuthenticationService;
import com.bangvan.service.JwtService;
import com.bangvan.service.VerificationCodeService;
import io.jsonwebtoken.JwtException; // Thêm import
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
// import java.time.Duration; // Không cần thiết nếu dùng TimeUnit
// import java.time.LocalDateTime; // Không cần thiết ở đây
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
        // Lưu refresh token mới vào Redis
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

        user.setEnabled(false); // User chưa active khi đăng ký

        user = userRepository.save(user);
        log.info("User {} saved with enabled=false", user.getUsername());

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);
        log.info("Cart created for user {}", user.getUsername());

        // Gửi OTP
        verificationCodeService.generateAndSendVerificationOtp(user);
        log.info("OTP sent to email {}", request.getEmail());


        log.info("Saving user to database completed for registration"); // Log rõ hơn
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    @Transactional // Quan trọng: Đảm bảo các thao tác Redis và DB là atomic
    public TokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) { // Đổi tên biến
        final String requestedRefreshToken = refreshTokenRequest.getRefreshToken();

        // 1. Kiểm tra token có hết hạn hoặc signature có hợp lệ không bằng cách extract username
        String username;
        try {
            username = jwtService.extractUsername(requestedRefreshToken);
        } catch (JwtException ex) { // Bắt lỗi chung của JWT (bao gồm cả ExpiredJwtException, SignatureException,...)
            log.warn("Invalid refresh token received: {}", ex.getMessage());
            throw new AppException(ErrorCode.INVALID_TOKEN, "Invalid or expired refresh token.");
        }

        // 2. Lấy UserDetails
        UserDetails userDetails = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)); // Lỗi nếu user không tồn tại

        // 3. Kiểm tra token trong Redis (Reuse Detection và Tồn tại)
        String redisKey = REFRESH_TOKEN_PREFIX + username;
        String storedToken = (String) redisTemplate.opsForValue().get(redisKey);

        if (storedToken == null) {
            // Trường hợp 1: Token không có trong Redis -> Đã bị thu hồi (logout) hoặc hết hạn Redis TTL
            // Hoặc Trường hợp 2 (Reuse Detection): Token đã được sử dụng (xoay vòng) và bị xóa.
            log.warn("Refresh token for user {} not found in Redis or already used (possible reuse attempt).", username);
            // Thu hồi tất cả refresh token của user để tăng cường bảo mật nếu nghi ngờ reuse
            // (Trong trường hợp này chỉ có 1 token / user nên chỉ cần đảm bảo nó không còn)
            // redisTemplate.delete(redisKey); // Đảm bảo xóa nếu còn sót lại
            throw new AppException(ErrorCode.TOKEN_EXPIRED, "Refresh token has expired, been revoked, or already used.");
        }

        if (!storedToken.equals(requestedRefreshToken)) {
            // Trường hợp 3: Token trong Redis không khớp với token gửi lên -> Bất thường, có thể là token cũ hoặc giả mạo
            log.warn("Provided refresh token does not match stored token for user {}. Possible tampering or old token.", username);
            // Thu hồi token hiện tại trong Redis vì có dấu hiệu bất thường
            redisTemplate.delete(redisKey);
            throw new AppException(ErrorCode.INVALID_TOKEN, "Refresh token mismatch. Please log in again.");
        }

        // --- Nếu đến đây, refresh token gửi lên là hợp lệ và khớp với Redis ---

        // 4. Kiểm tra lại lần nữa với logic isTokenValid (dù hơi thừa nhưng để chắc chắn)
        // Lưu ý: isTokenValid cũng kiểm tra cả expired time bên trong token payload
        if (!jwtService.isTokenValid(requestedRefreshToken, userDetails)) {
            log.warn("Refresh token for user {} failed final validation despite matching Redis.", username);
            redisTemplate.delete(redisKey); // Xóa token khỏi Redis
            throw new AppException(ErrorCode.INVALID_TOKEN, "Invalid refresh token.");
        }

        // --- Token hoàn toàn hợp lệ ---

        // 5. Tạo Access Token mới
        String newAccessToken = jwtService.generateToken(userDetails);

        // 6. Tạo Refresh Token MỚI (Rotation)
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        // 7. Lưu Refresh Token MỚI vào Redis, thay thế token cũ
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, refreshTokenExpirationMilliseconds, TimeUnit.MILLISECONDS);
        log.info("Rotated refresh token for user {} stored in Redis.", username);

        // 8. Trả về cả hai token mới
        long now = System.currentTimeMillis();
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken) // Trả về refresh token mới
                .expiredTime(new Timestamp(now + jwtService.getExpirationTime())) // Thời gian hết hạn của access token
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
        // Nếu bạn implement blacklist access token, hãy thêm logic blacklist ở đây (ví dụ: lưu JTI của access token vào Redis với TTL bằng thời gian còn lại của token)
    }
}