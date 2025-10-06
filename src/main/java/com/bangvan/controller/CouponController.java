package com.bangvan.controller;

import com.bangvan.dto.request.coupon.ApplyCouponRequest;
import com.bangvan.dto.request.coupon.CouponRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupon", description = "Coupon Management API")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new coupon", description = "Endpoint for admins to create a new coupon.")
    public ResponseEntity<ApiResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Coupon created successfully",
                couponService.createCoupon(request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing coupon", description = "Endpoint for admins to update a coupon.")
    public ResponseEntity<ApiResponse> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon updated successfully",
                couponService.updateCoupon(id, request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a coupon", description = "Endpoint for admins to delete a coupon.")
    public ResponseEntity<ApiResponse> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon deleted successfully",
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all coupons", description = "Endpoint for admins to get a paginated list of all coupons.")
    public ResponseEntity<ApiResponse> getAllCoupons(
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "startDate", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC", required = false) String sortDir) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupons fetched successfully",
                couponService.getAllCoupons(pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

//    @PostMapping("/apply")
//    @PreAuthorize("hasRole('USER')")
//    @Operation(summary = "Apply a coupon to the cart", description = "Endpoint for users to apply a coupon to their shopping cart.")
//    public ResponseEntity<ApiResponse> applyCoupon(@Valid @RequestBody ApplyCouponRequest request, Principal principal) {
//        ApiResponse apiResponse = ApiResponse.success(
//                HttpStatus.OK.value(),
//                "Coupon applied successfully",
//                couponService.applyCoupon(request, principal)
//        );
//        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
//    }
}