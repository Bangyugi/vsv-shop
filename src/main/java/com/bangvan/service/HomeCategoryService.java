package com.bangvan.service;

import com.bangvan.dto.request.category.CategoryRequest;
import com.bangvan.dto.response.category.HomeCategoryResponse;
import java.util.List;

public interface HomeCategoryService {
    HomeCategoryResponse createHomeCategory(CategoryRequest request);
    List<HomeCategoryResponse> getAllHomeCategories();
    HomeCategoryResponse getHomeCategoryById(Long id);
    HomeCategoryResponse updateHomeCategory(Long id, CategoryRequest request);
    void deleteHomeCategory(Long id);
}