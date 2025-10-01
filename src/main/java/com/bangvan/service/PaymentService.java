package com.bangvan.service;

import com.bangvan.dto.response.payment.PaymentLinkResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface PaymentService {
    PaymentLinkResponse createVnpayPaymentLink(Long orderId, HttpServletRequest request);

    @Transactional
    Map<String, String> processVnpayCallback(HttpServletRequest request);
}
