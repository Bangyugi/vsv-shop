package com.bangvan.service;


import com.bangvan.dto.request.review.ReviewRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.review.ReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface ReviewService {

    @Transactional
    ReviewResponse createReview(Long productId, ReviewRequest request, Principal principal);

    @Transactional
    ReviewResponse updateReview(Long reviewId, ReviewRequest request, Principal principal);

    @Transactional
    void deleteReview(Long reviewId, Principal principal);

    PageCustomResponse<ReviewResponse> getReviewsByProductId(Long productId, Pageable pageable);
}