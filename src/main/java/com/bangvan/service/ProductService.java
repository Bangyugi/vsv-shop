package com.bangvan.service;

import com.bangvan.dto.request.product.CreateProductRequest;
import com.bangvan.dto.request.product.UpdateProductRequest;
import com.bangvan.dto.request.product.UpdateStockRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.product.ProductResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;

public interface ProductService {
    @Transactional
    ProductResponse createProduct(CreateProductRequest request, Principal principal);

    ProductResponse getProductById(Long productId);



    PageCustomResponse<ProductResponse> getAllProducts(
            String keyword, Long categoryId, Long sellerId,
            BigDecimal minPrice, BigDecimal maxPrice, String color, String size,
            Double minRating, // Thêm tham số rating
            Pageable pageable
    );

    String validateSortByField(String sortBy);
    @Transactional
    ProductResponse updateProductById(Long productId, UpdateProductRequest request, Principal principal);

    @Transactional
    String deleteProductById(Long productId, Principal principal);

    Integer calculateDiscountPercentage(BigDecimal price, BigDecimal sellingPrice);



    @Transactional
    ProductResponse updateProductStock(Long variantId, UpdateStockRequest request, Principal principal);

    PageCustomResponse<ProductResponse> findProductBySeller(Long sellerId, Pageable pageable);

    PageCustomResponse<ProductResponse> searchProduct(String keyword, Pageable pageable);

    PageCustomResponse<ProductResponse> findProductByCategory(Long categoryId, Pageable pageable);
}
