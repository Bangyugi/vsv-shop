package com.bangvan.service;

import com.bangvan.dto.request.category.DealRequest;
import com.bangvan.dto.response.category.DealResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DealService {
    @Transactional
    DealResponse createDeal(DealRequest request);

    List<DealResponse> getAllDeals();

    DealResponse getDealById(Long id);

    @Transactional
    DealResponse updateDeal(Long id, DealRequest request);

    @Transactional
    void deleteDeal(Long id);
}