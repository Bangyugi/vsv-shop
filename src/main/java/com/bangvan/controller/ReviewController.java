package com.bangvan.controller;

import com.bangvan.dto.request.review.ReviewRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "APIs for managing product reviews (Requires Authentication)")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new review", description = "Endpoint for authenticated users to add a review to a product they have purchased.")
    public ResponseEntity<ApiResponse> createReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request,
            Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Review created successfully",
                reviewService.createReview(productId, request, principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update an existing review", description = "Endpoint for users to update their own review.")
    public ResponseEntity<ApiResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Review updated successfully",
                reviewService.updateReview(reviewId, request, principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Delete a review", description = "Endpoint for a user to delete their own review or for an admin to delete any review.")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long reviewId, Principal principal) {
        reviewService.deleteReview(reviewId, principal);
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Review deleted successfully",
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}