package com.bangvan.service.impl;

import com.bangvan.dto.request.review.ReviewRequest;
import com.bangvan.dto.response.PageCustomResponse;

import com.bangvan.dto.response.review.ReviewResponse;
import com.bangvan.dto.response.user.UserResponse;

import com.bangvan.entity.Product;
import com.bangvan.entity.Review;
import com.bangvan.entity.User;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.OrderRepository;
import com.bangvan.repository.ProductRepository;
import com.bangvan.repository.ReviewRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.ReviewService;
import com.bangvan.utils.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public ReviewResponse createReview(Long productId, ReviewRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

        boolean hasPurchased = orderRepository.findByUserAndOrderStatus(user, OrderStatus.DELIVERED, Pageable.unpaged())
                .getContent().stream()
                .anyMatch(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProductTitle().equals(product.getTitle())));

        if (!hasPurchased) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "You can only review products you have purchased.");
        }

        reviewRepository.findByUserIdAndProductId(user.getId(), productId).ifPresent(review -> {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS, "You have already reviewed this product.");
        });

        Review review = modelMapper.map(request, Review.class);
        review.setUser(user);
        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);
        return mapReviewToResponse(savedReview);
    }

    @Transactional
    @Override
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "ID", reviewId));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "You can only update your own reviews.");
        }

        modelMapper.map(request, review);
        Review updatedReview = reviewRepository.save(review);
        return mapReviewToResponse(updatedReview);
    }

    @Transactional
    @Override
    public void deleteReview(Long reviewId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "ID", reviewId));

        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!review.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "You can only delete your own reviews or you must be an admin.");
        }

        reviewRepository.delete(review);
    }

    @Override
    public PageCustomResponse<ReviewResponse> getReviewsByProductId(Long productId, Pageable pageable) {
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);
        List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
                .map(this::mapReviewToResponse)
                .collect(Collectors.toList());

        return PageCustomResponse.<ReviewResponse>builder()
                .pageNo(reviewPage.getNumber() + 1)
                .pageSize(reviewPage.getSize())
                .totalPages(reviewPage.getTotalPages())
                .totalElements(reviewPage.getTotalElements())
                .pageContent(reviewResponses)
                .build();
    }

    private ReviewResponse mapReviewToResponse(Review review) {
        ReviewResponse response = modelMapper.map(review, ReviewResponse.class);
        response.setUser(modelMapper.map(review.getUser(), UserResponse.class));
        return response;
    }
}