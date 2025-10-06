package com.bangvan.controller;

import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.WishListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Wishlist Management API")
public class WishListController {

    private final WishListService wishListService;

    @PostMapping("/add/{productId}")
    @Operation(summary = "Add a product to the wishlist", description = "Endpoint for users to add a product to their wishlist.")
    public ResponseEntity<ApiResponse> addProductToWishlist(@PathVariable Long productId, Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Product added to wishlist successfully",
                wishListService.addProductToWishlist(productId, principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "Remove a product from the wishlist", description = "Endpoint for users to remove a product from their wishlist.")
    public ResponseEntity<ApiResponse> removeProductFromWishlist(@PathVariable Long productId, Principal principal) {
        wishListService.removeProductFromWishlist(productId, principal);
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Product removed from wishlist successfully",
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Get the user's wishlist", description = "Endpoint for users to retrieve their wishlist.")
    public ResponseEntity<ApiResponse> getUserWishlist(Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Wishlist retrieved successfully",
                wishListService.getUserWishlist(principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}