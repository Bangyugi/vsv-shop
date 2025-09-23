package com.bangvan.controller;

import com.bangvan.dto.request.verify.VerificationRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.entity.VerificationCode;
import com.bangvan.service.VerificationCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/verify")
@RequiredArgsConstructor
@Tag(name = "Verify", description = "Verify API")
@Slf4j
public class VerificationCodeController {

    private final VerificationCodeService verificationCodeService;

    @Operation(summary = "Send Verification Code", description = "Send Verification Code")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendVerificationCode(@RequestParam(name = "email") String email){
        verificationCodeService.sendVerificationOtpEmail(email);
        return ResponseEntity.ok(ApiResponse.success(200, "Verification code sent successfully", null));
    }

    @Operation(summary = "Verify Verification Code", description = "Verify Verification Code")
    @PostMapping()
    public ResponseEntity<ApiResponse> verifyVerificationCode(@RequestBody VerificationRequest request){
        verificationCodeService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(200, "Verification code verified successfully", null));
    }

}
