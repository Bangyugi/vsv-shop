package com.bangvan.exception;


import com.bangvan.dto.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;


    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse> handleAppException(AppException exception, WebRequest webRequest) {
        ErrorCode errorCode = exception.getErrorCode();
        log.warn("AppException Handled: Code={}, Message={}", errorCode.getCode(), exception.getMessage());

        ApiResponse apiResponse = ApiResponse.error(
                errorCode.getCode(),
                exception.getMessage()
        );

        return new ResponseEntity<>(apiResponse, errorCode.getStatus());
    }


    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> handleExpiredJwtException(ExpiredJwtException exception, WebRequest webRequest) {
        log.warn("JWT expired, handled by GlobalExceptionHandler: {}", exception.getMessage());

        ApiResponse apiResponse = ApiResponse.error(
                401,

                "Access token has expired. Please refresh or log in again."

        );

        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }



    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationException(AuthenticationException exception, WebRequest webRequest) {
        log.warn("Authentication failed, handled by GlobalExceptionHandler: {}", exception.getMessage());
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        ApiResponse apiResponse = ApiResponse.error(
                errorCode.getCode(),
                exception.getMessage()
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
    }



    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException exception, WebRequest webRequest) {
        log.warn("Access denied, handled by GlobalExceptionHandler: {}", exception.getMessage());
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ApiResponse apiResponse = ApiResponse.error(
                errorCode.getCode(),
                exception.getMessage()
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGlobalException(Exception exception, WebRequest webRequest) {
        log.error("An unexpected error occurred: {}", exception.getMessage(), exception);

        String message = "An internal server error occurred. Please try again later.";
        if (!"prod".equalsIgnoreCase(activeProfile)) {
            message = exception.getMessage();
        }

        ApiResponse apiResponse= ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}