package com.bangvan.service.impl;

import com.bangvan.dto.response.seller.NotificationSummaryResponse;
import com.bangvan.entity.Seller;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.NotificationRepository;
import com.bangvan.repository.SellerRepository;
import com.bangvan.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SellerRepository sellerRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationSummaryResponse getNotificationSummary(Principal principal) {
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));

        // Gọi query tùy chỉnh hiệu quả
        return notificationRepository.getSummaryBySeller(seller);
    }
}