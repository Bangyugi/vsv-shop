package com.bangvan.service.impl;

import com.bangvan.dto.request.category.DealRequest;
import com.bangvan.dto.response.category.DealResponse;
import com.bangvan.entity.Deal;
import com.bangvan.entity.HomeCategory;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.DealRepository;
import com.bangvan.repository.HomeCategoryRepository;
import com.bangvan.service.DealService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final DealRepository dealRepository;
    private final HomeCategoryRepository homeCategoryRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public DealResponse createDeal(DealRequest request) {
        HomeCategory homeCategory = homeCategoryRepository.findById(request.getHomeCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("HomeCategory", "id", request.getHomeCategoryId()));

        Deal deal = new Deal();
        deal.setDiscount(request.getDiscount());
        deal.setCategory(homeCategory);

        Deal savedDeal = dealRepository.save(deal);
        return modelMapper.map(savedDeal, DealResponse.class);
    }

    @Override
    public List<DealResponse> getAllDeals() {
        return dealRepository.findAll().stream()
                .map(deal -> modelMapper.map(deal, DealResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public DealResponse getDealById(Long id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));
        return modelMapper.map(deal, DealResponse.class);
    }

    @Transactional
    @Override
    public DealResponse updateDeal(Long id, DealRequest request) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal", "id", id));

        HomeCategory homeCategory = homeCategoryRepository.findById(request.getHomeCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("HomeCategory", "id", request.getHomeCategoryId()));

        deal.setDiscount(request.getDiscount());
        deal.setCategory(homeCategory);

        Deal updatedDeal = dealRepository.save(deal);
        return modelMapper.map(updatedDeal, DealResponse.class);
    }

    @Transactional
    @Override
    public void deleteDeal(Long id) {
        if (!dealRepository.existsById(id)) {
            throw new ResourceNotFoundException("Deal", "id", id);
        }
        dealRepository.deleteById(id);
    }
}