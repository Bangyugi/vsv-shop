package com.bangvan.service;

import com.bangvan.dto.request.category.HomeCategoryRequest;
import com.bangvan.dto.response.category.HomeCategoryResponse;
import java.util.List;

public interface HomeCategoryService {
    HomeCategoryResponse createHomeCategory(HomeCategoryRequest request);
    List<HomeCategoryResponse> getAllHomeCategories();
    HomeCategoryResponse getHomeCategoryById(Long id);
    HomeCategoryResponse updateHomeCategory(Long id, HomeCategoryRequest request);
    void deleteHomeCategory(Long id);
}