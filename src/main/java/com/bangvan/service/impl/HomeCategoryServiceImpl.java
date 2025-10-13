package com.bangvan.service.impl;

import com.bangvan.dto.request.category.HomeCategoryRequest;
import com.bangvan.dto.response.category.HomeCategoryResponse;
import com.bangvan.entity.HomeCategory;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.HomeCategoryRepository;
import com.bangvan.service.HomeCategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeCategoryServiceImpl implements HomeCategoryService {

    private final HomeCategoryRepository homeCategoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public HomeCategoryResponse createHomeCategory(HomeCategoryRequest request) {
        HomeCategory homeCategory = modelMapper.map(request, HomeCategory.class);
        HomeCategory savedHomeCategory = homeCategoryRepository.save(homeCategory);
        return modelMapper.map(savedHomeCategory, HomeCategoryResponse.class);
    }

    @Override
    public List<HomeCategoryResponse> getAllHomeCategories() {
        return homeCategoryRepository.findAll().stream()
                .map(category -> modelMapper.map(category, HomeCategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public HomeCategoryResponse getHomeCategoryById(Long id) {
        HomeCategory homeCategory = homeCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HomeCategory", "id", id));
        return modelMapper.map(homeCategory, HomeCategoryResponse.class);
    }

    @Override
    public HomeCategoryResponse updateHomeCategory(Long id, HomeCategoryRequest request) {
        HomeCategory homeCategory = homeCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HomeCategory", "id", id));
        modelMapper.map(request, homeCategory);
        HomeCategory updatedHomeCategory = homeCategoryRepository.save(homeCategory);
        return modelMapper.map(updatedHomeCategory, HomeCategoryResponse.class);
    }

    @Override
    public void deleteHomeCategory(Long id) {
        if (!homeCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("HomeCategory", "id", id);
        }
        homeCategoryRepository.deleteById(id);
    }
}