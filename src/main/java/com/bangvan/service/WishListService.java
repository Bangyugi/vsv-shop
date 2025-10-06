package com.bangvan.service;

import com.bangvan.dto.response.wishlist.WishListResponse;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface WishListService {
    @Transactional
    WishListResponse addProductToWishlist(Long productId, Principal principal);

    @Transactional
    void removeProductFromWishlist(Long productId, Principal principal);

    WishListResponse getUserWishlist(Principal principal);
}
