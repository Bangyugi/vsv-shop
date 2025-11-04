package com.bangvan.service.impl;

import com.bangvan.dto.request.category.CategoryRequest;
import com.bangvan.dto.response.category.CategoryResponse;
import com.bangvan.entity.Category;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.CategoryRepository;
import com.bangvan.repository.ProductRepository;
import com.bangvan.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("ParentCategory", "id", request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
            category.setLevel(parentCategory.getLevel() + 1);
        }

        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryResponse.class);
    }

    @Override
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        category.setName(request.getName());

        if (request.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("ParentCategory", "id", request.getParentCategoryId()));
            category.setParentCategory(parentCategory);
            category.setLevel(parentCategory.getLevel() + 1);
        } else {
            category.setParentCategory(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return modelMapper.map(updatedCategory, CategoryResponse.class);
    }

    @Transactional
    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));


        List<Category> childCategories = categoryRepository.findByParentCategory_Id(categoryId);
        if (!childCategories.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_HAS_CHILDREN);
        }


        productRepository.findByCategoryId(categoryId).forEach(product -> {
            product.setCategory(null);
            productRepository.save(product);
        });


        categoryRepository.delete(category);
        return "Category with ID " + categoryId + " has been deleted successfully.";
    }

    @Override
    public List<CategoryResponse> findAllLevel3Subcategories(Long parentCategoryId) {
        log.info("Finding all level 3 subcategories for parent category ID: {}", parentCategoryId);


        List<Category> allCategories = categoryRepository.findAll();
        Map<Long, List<Category>> childrenMap = allCategories.stream()
                .filter(cat -> cat.getParentCategory() != null)
                .collect(Collectors.groupingBy(cat -> cat.getParentCategory().getId()));


        List<Category> level3Categories = new ArrayList<>();
        Queue<Category> categoriesToProcess = new LinkedList<>();


        Category initialCategory = allCategories.stream()
                .filter(cat -> cat.getId().equals(parentCategoryId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", parentCategoryId));

        categoriesToProcess.offer(initialCategory);


        while (!categoriesToProcess.isEmpty()) {
            Category currentCategory = categoriesToProcess.poll();


            if (currentCategory.getLevel() == 3) {
                level3Categories.add(currentCategory);

                continue;
            }


            List<Category> children = childrenMap.getOrDefault(currentCategory.getId(), Collections.emptyList());


            for (Category child : children) {
                if (child.getLevel() <= 3) {
                    categoriesToProcess.offer(child);
                }
            }
        }

        log.info("Found {} level 3 subcategories for parent category ID: {}", level3Categories.size(), parentCategoryId);


        return level3Categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponse.class))
                .collect(Collectors.toList());
    }
}
