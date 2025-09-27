package com.bangvan.service;

import com.bangvan.dto.request.seller.BecomeSellerRequest;
import com.bangvan.dto.request.seller.UpdateSellerRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.seller.SellerResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface SellerService {
    SellerResponse getProfile(Principal principal);

    @Transactional
    SellerResponse becomeSeller(BecomeSellerRequest request, Principal principal);

    @Transactional
    SellerResponse updateSeller(UpdateSellerRequest request, Principal principal);

    @Transactional
    String deleteSeller(Principal principal);

    PageCustomResponse<SellerResponse> getAllSellers(Pageable pageable);
}
