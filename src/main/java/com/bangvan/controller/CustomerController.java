package com.bangvan.controller;

import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.HomeService;
import com.bangvan.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "APIs for Customer browsing experience (Public)")
public class CustomerController {

    private final HomeService homeService;
    private final ReviewService reviewService;

    @GetMapping("/home")
    @Operation(summary = "Get Home Page Data", description = "Endpoint to retrieve all data needed for the home page layout.")
    public ResponseEntity<ApiResponse> getHomePageData() {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Home page data fetched successfully",
                homeService.createHomePageData()
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/products/{productId}/reviews")
    @Operation(summary = "Get Reviews by Product ID", description = "Public endpoint to get a paginated list of reviews for a specific product.")
    public ResponseEntity<ApiResponse> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC", required = false) String sortDir
    ) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Reviews fetched successfully",
                reviewService.getReviewsByProductId(productId, pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}