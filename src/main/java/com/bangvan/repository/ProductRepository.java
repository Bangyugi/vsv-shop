package com.bangvan.repository;

import com.bangvan.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    Iterable<Product> findByCategoryId(Long categoryId);

    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    Page<Product> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
