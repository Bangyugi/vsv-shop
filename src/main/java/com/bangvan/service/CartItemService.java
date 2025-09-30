package com.bangvan.service;

import com.bangvan.dto.request.cart.UpdateCartItemRequest;
import com.bangvan.dto.response.cart.CartItemResponse;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface CartItemService {
    @Transactional
    void removeCartItem(Principal principal, Long cartItemId);

    @Transactional
    CartItemResponse updateCartItem(Principal principal, Long cartItemId, UpdateCartItemRequest request);

    CartItemResponse findCartItemById(Long cartItemId);
}
