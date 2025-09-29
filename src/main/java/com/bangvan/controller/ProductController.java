package com.bangvan.controller;

import com.bangvan.dto.request.product.CreateProductRequest;
import com.bangvan.dto.request.product.UpdateProductRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Create Product", description = "Create Product API")
    @PreAuthorize("hasRole('SELLER')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody CreateProductRequest request, Principal principal) {
        log.info("Request: {}", request);
        ApiResponse apiResponse = ApiResponse.success(201, "Product created successfully", productService.createProduct(request, principal));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get a product by its ID", description = "Endpoint to fetch product details by ID")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long productId) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Product found successfully",
                productService.getProductById(productId)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get all products with pagination and sorting", description = "Endpoint to fetch a paginated list of all products")
    public ResponseEntity<ApiResponse> getAllProducts(
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "size", required = false) String size,
            @RequestParam(value = "seller", required = false) Long sellerId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) Long categoryId,
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Products fetched successfully",
                productService.getAllProducts(minPrice, maxPrice, color, size, sellerId, keyword, categoryId, pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update a product by its ID", description = "Endpoint for sellers to update their own product")
    public ResponseEntity<ApiResponse> updateProductById(
            @PathVariable Long productId,
            @Valid @RequestBody UpdateProductRequest request,
            Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Product updated successfully",
                productService.updateProductById(productId, request, principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Delete a product by its ID", description = "Endpoint for sellers to delete their own product")
    public ResponseEntity<ApiResponse> deleteProductById(
            @PathVariable Long productId,
            Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                productService.deleteProductById(productId, principal),
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PatchMapping("/{productId}/stock")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update a product's stock", description = "Endpoint for sellers to update their own product's stock")
    public ResponseEntity<ApiResponse> updateProductStock(
            @PathVariable Long productId,
            @RequestParam Integer quantity,
            Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Product stock updated successfully",
                productService.updateProductStock(productId, quantity, principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get products by seller ID", description = "Endpoint to fetch products by seller ID")
    public ResponseEntity<ApiResponse> getProductsBySeller(
            @PathVariable Long sellerId,
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Products fetched successfully",
                productService.findProductBySeller(sellerId, pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Operation(summary = "Search for products", description = "Endpoint to search for products by keyword")
    public ResponseEntity<ApiResponse> searchProduct(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Products fetched successfully",
                productService.searchProduct(keyword, pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
