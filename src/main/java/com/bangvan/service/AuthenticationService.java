package com.bangvan.service;

import com.bangvan.dto.request.auth.LoginRequest;
import com.bangvan.dto.request.auth.RefreshTokenRequest;
import com.bangvan.dto.request.auth.RegisterRequest;
import com.bangvan.dto.response.auth.TokenResponse;
import com.bangvan.dto.response.user.UserResponse;
import jakarta.mail.MessagingException;

public interface AuthenticationService {



    TokenResponse login(LoginRequest loginRequest);

    UserResponse register(RegisterRequest request) throws MessagingException;

    TokenResponse refreshToken(RefreshTokenRequest refreshToken);
}
