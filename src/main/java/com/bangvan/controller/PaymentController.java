package com.bangvan.controller;


import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Payment Management API")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/vnpay/{orderId}")
    @Operation(summary = "Create VNPAY Payment Link", description = "Endpoint to create a VNPAY payment link for an order")
    public ResponseEntity<ApiResponse> createVnpayPaymentLink(@PathVariable Long orderId, HttpServletRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Payment link created successfully",
                paymentService.createVnpayPaymentLink(orderId, request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    // Endpoint này sẽ xử lý callback từ VNPAY
    @GetMapping("/vnpay-callback")
    public ResponseEntity<Map<String, String>> vnpayCallback(HttpServletRequest request) {
        Map<String, String> response = paymentService.processVnpayCallback(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}