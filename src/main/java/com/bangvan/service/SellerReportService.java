package com.bangvan.service;

import com.bangvan.dto.response.seller.SellerReportResponse;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface SellerReportService {
    SellerReportResponse getMySellerReport(Principal principal);

    SellerReportResponse getReportBySellerId(Long sellerId);

    @Transactional
    SellerReportResponse generateSellerReport(Long sellerId);
}
