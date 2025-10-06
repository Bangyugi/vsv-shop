package com.bangvan.service;

import com.bangvan.dto.request.cart.AddItemToCartRequest;
import com.bangvan.dto.request.coupon.ApplyCouponRequest;
import com.bangvan.dto.response.cart.CartResponse;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface CartService {
    @Transactional
    CartResponse addItemToCart(Principal principal, AddItemToCartRequest request);

    CartResponse findCartByUser(Principal principal);

    @Transactional
    CartResponse applyCoupon(ApplyCouponRequest request, Principal principal);

}
