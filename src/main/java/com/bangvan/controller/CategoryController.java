package com.bangvan.controller;


import com.bangvan.dto.request.category.CategoryRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category Management API")
public class CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PostMapping
    @Operation(summary = "Create a new category", description = "Endpoint for admins to create a new product category.")
    public ResponseEntity<ApiResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Category created successfully",
                categoryService.createCategory(request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Endpoint to fetch a list of all product categories.")
    public ResponseEntity<ApiResponse> getAllCategories() {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Categories fetched successfully",
                categoryService.getAllCategories()
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get a category by ID", description = "Endpoint to fetch details of a specific category by its ID.")
    public ResponseEntity<ApiResponse> getCategoryById(@PathVariable Long categoryId) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Category found successfully",
                categoryService.getCategoryById(categoryId)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    @Operation(summary = "Update a category", description = "Endpoint for admins to update an existing product category.")
    public ResponseEntity<ApiResponse> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Category updated successfully",
                categoryService.updateCategory(categoryId, request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete a category", description = "Endpoint for admins to delete a product category.")
    public ResponseEntity<ApiResponse> deleteCategory(@PathVariable Long categoryId) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                categoryService.deleteCategory(categoryId),
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }



    @GetMapping("/{parentCategoryId}/level3-subcategories")
    @Operation(summary = "Get all level 3 subcategories", description = "Endpoint to fetch all level 3 subcategories under a given parent category (level 1 or 2).")
    public ResponseEntity<ApiResponse> findAllLevel3Subcategories(@PathVariable Long parentCategoryId) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Level 3 subcategories fetched successfully",
                categoryService.findAllLevel3Subcategories(parentCategoryId)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}