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
import com.bangvan.repository.VerificationCodeRepository;
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
import java.time.LocalDateTime;
import java.util.Set;

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
    private final VerificationCodeRepository verificationCodeRepository;
    private final VerificationCodeService verificationCodeService;



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
            throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
        }

        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

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

        user = userRepository.save(user);
        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);


        String otp = verificationCodeService.generateVerificationCode();
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(request.getEmail());
        verificationCode.setExpiredTime(LocalDateTime.now().plusMinutes(15));
        verificationCode.setUser(user);

        verificationCodeRepository.save(verificationCode);
//        verificationCodeService.sendVerificationOtpEmail(request.getEmail());

        log.info("Saving user to database");
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest refreshToken) {
        if (jwtService.isTokenExpired(refreshToken.getRefreshToken())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
        String username = jwtService.extractUsername(refreshToken.getRefreshToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);


        long now = System.currentTimeMillis();
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiredTime(new Timestamp(now + jwtService.getExpirationTime()))
                .build();
    }



}
