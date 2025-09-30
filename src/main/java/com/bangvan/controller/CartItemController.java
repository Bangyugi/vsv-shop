package com.bangvan.controller;

import com.bangvan.dto.request.cart.UpdateCartItemRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.CartItemService;
import com.bangvan.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart-items")
public class CartItemController {


    private final CartItemService cartItemService;

    @PutMapping("/{cartItemId}")
    @Operation(summary = "Update item in cart", description = "Update the quantity of a product in the current user's shopping cart")
    public ResponseEntity<ApiResponse> updateCartItem(
            Principal principal,
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Item updated successfully",
                cartItemService.updateCartItem(principal, cartItemId, request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @DeleteMapping("/{cartItemId}")
    @Operation(summary = "Remove item from cart", description = "Remove an item from the current user's shopping cart")
    public ResponseEntity<ApiResponse> removeCartItem(
            Principal principal,
            @PathVariable Long cartItemId) {
        cartItemService.removeCartItem(principal, cartItemId);
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Item removed successfully",
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{cartItemId}")
    @Operation(summary = "Get item from cart", description = "Get an item from the current user's shopping cart")
    public ResponseEntity<ApiResponse> getCartItem(
            @PathVariable Long cartItemId) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Item found successfully",
                cartItemService.findCartItemById(cartItemId)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}
