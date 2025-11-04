package com.bangvan.config;

import com.bangvan.dto.response.ApiResponse;
import com.bangvan.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntrypoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {


        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;


        log.warn("Authentication failed for request URI {}: {}", request.getRequestURI(), authException.getMessage());


        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");


        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(authException.getMessage())

                .build();


        try {
            response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        } catch (IOException e) {
            log.error("Error writing authentication error response", e);

            throw e;
        }
    }
}