package com.bangvan.service;

import com.bangvan.dto.request.product.CreateProductRequest;
import com.bangvan.dto.request.product.UpdateProductRequest;
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



    PageCustomResponse<ProductResponse> getAllProducts(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    @Transactional
    ProductResponse updateProductById(Long productId, UpdateProductRequest request, Principal principal);

    @Transactional
    String deleteProductById(Long productId, Principal principal);

    Integer calculateDiscountPercentage(BigDecimal price, BigDecimal sellingPrice);

    @Transactional
    ProductResponse updateProductStock(Long productId, Integer quantity, Principal principal);

    PageCustomResponse<ProductResponse> findProductBySeller(Long sellerId, Pageable pageable);

    PageCustomResponse<ProductResponse> searchProduct(String keyword, Pageable pageable);
}
