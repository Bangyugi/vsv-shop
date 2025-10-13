package com.bangvan.service;


import com.bangvan.dto.request.category.HomeCategoryRequest;
import com.bangvan.dto.response.category.CategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {

    @Transactional
    CategoryResponse createCategory(HomeCategoryRequest.CategoryRequest request);

    CategoryResponse getCategoryById(Long categoryId);

    List<CategoryResponse> getAllCategories();

    @Transactional
    CategoryResponse updateCategory(Long categoryId, HomeCategoryRequest.CategoryRequest request);

    @Transactional
    String deleteCategory(Long categoryId);
}
