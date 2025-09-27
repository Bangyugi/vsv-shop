package com.bangvan.service;


import com.bangvan.dto.request.product.CategoryRequest;
import com.bangvan.dto.response.product.CategoryResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CategoryService {

    @Transactional
    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse getCategoryById(Long categoryId);

    List<CategoryResponse> getAllCategories();

    @Transactional
    CategoryResponse updateCategory(Long categoryId, CategoryRequest request);

    @Transactional
    String deleteCategory(Long categoryId);
}
