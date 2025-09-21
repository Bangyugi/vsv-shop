package com.bangvan.service;

import com.bangvan.dto.request.auth.LoginRequest;
import com.bangvan.dto.request.auth.RefreshTokenRequest;
import com.bangvan.dto.request.auth.RegisterRequest;
import com.bangvan.dto.response.auth.TokenResponse;
import com.bangvan.dto.response.user.UserResponse;

public interface AuthenticationService {

    TokenResponse login(LoginRequest loginRequest);

    UserResponse register(RegisterRequest request);

    TokenResponse refreshToken(RefreshTokenRequest refreshToken);
}
