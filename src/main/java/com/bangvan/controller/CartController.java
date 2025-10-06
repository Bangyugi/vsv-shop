package com.bangvan.controller;

import com.bangvan.dto.request.cart.AddItemToCartRequest;
import com.bangvan.dto.request.coupon.ApplyCouponRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.CartService;
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
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart API")
public class CartController {
    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Find Cart By User", description = "Find Cart By User API")
    public ResponseEntity<ApiResponse> findCartByUser(Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Cart found successfully",
                cartService.findCartByUser(principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/add")
    @Operation(summary = "Add item to cart", description = "Add a product to the current user's shopping cart")
    public ResponseEntity<ApiResponse> addItemToCart(Principal principal, @Valid @RequestBody AddItemToCartRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Item added to cart successfully",
                cartService.addItemToCart(principal, request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PostMapping("/apply-coupon")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Apply a coupon to the cart", description = "Endpoint for users to apply a coupon to their shopping cart.")
    public ResponseEntity<ApiResponse> applyCoupon(@Valid @RequestBody ApplyCouponRequest request, Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Coupon applied successfully",
                cartService.applyCoupon(request, principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}