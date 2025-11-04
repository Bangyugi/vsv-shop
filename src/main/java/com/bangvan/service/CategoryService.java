package com.bangvan.service;


import com.bangvan.dto.request.category.CategoryRequest;
import com.bangvan.dto.response.category.CategoryResponse;
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

    List<CategoryResponse> findAllLevel3Subcategories(Long parentCategoryId);
}
