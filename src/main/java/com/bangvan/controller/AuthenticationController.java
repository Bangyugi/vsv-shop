package com.bangvan.controller;

import com.bangvan.dto.request.auth.LoginRequest;
import com.bangvan.dto.request.auth.RefreshTokenRequest;
import com.bangvan.dto.request.auth.RegisterRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Login", description = "Login API")
    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse>login (@RequestBody LoginRequest loginRequest){
        ApiResponse authentication =  ApiResponse.success(200, "User logged in successfully", authenticationService.login(loginRequest));

        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @Operation(summary = "Register", description = "Register API")
    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponse> register (@RequestBody RegisterRequest request){
        ApiResponse authentication =  ApiResponse.success(200, "User registered successfully", authenticationService.register(request));

        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @Operation(summary = "Refresh Token", description = "Refresh Token API")
    @PostMapping(value = "/refreshtoken")
    public ResponseEntity<ApiResponse> refreshToken (@RequestBody RefreshTokenRequest refreshToken){
        ApiResponse authentication =  ApiResponse.success(200, "User logged in successfully", authenticationService.refreshToken(refreshToken));

        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

}
