package com.bangvan.service;

import com.bangvan.dto.request.auth.LoginRequest;
import com.bangvan.dto.request.auth.RegisterRequest;
import com.bangvan.dto.response.TokenResponse;
import com.bangvan.dto.response.user.UserResponse;

public interface AuthenticationService {

    TokenResponse login(LoginRequest loginRequest);

    UserResponse register(RegisterRequest request);

    TokenResponse refreshToken(String refreshToken);
}
