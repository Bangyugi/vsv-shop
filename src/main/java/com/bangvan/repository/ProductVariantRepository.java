package com.bangvan.repository;

import com.bangvan.entity.ProductVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProductVariant> findById(Long id);
}
