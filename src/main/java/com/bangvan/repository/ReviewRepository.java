package com.bangvan.repository;

import com.bangvan.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {


    @Query("SELECT r FROM Review r WHERE r.orderItem.variant.product.id = :productId")
    Page<Review> findByProductId(@Param("productId") Long productId, Pageable pageable);

    boolean existsByOrderItemId(Long orderItemId);




    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.orderItem.variant.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

}