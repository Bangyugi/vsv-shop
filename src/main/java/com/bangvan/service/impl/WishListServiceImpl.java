package com.bangvan.service.impl;


import com.bangvan.dto.response.wishlist.WishListResponse;
import com.bangvan.entity.Product;
import com.bangvan.entity.User;
import com.bangvan.entity.WishList;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.ProductRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.repository.WishListRepository;

import com.bangvan.service.WishListService;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public WishListResponse addProductToWishlist(Long productId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));

        WishList wishlist = wishlistRepository.findByUser(user).orElseGet(() -> {
            WishList newWishlist = new WishList();
            newWishlist.setUser(user);
            return newWishlist;
        });

        wishlist.getProducts().add(product);
        wishlistRepository.save(wishlist);

        return modelMapper.map(wishlist, WishListResponse.class);
    }

    @Transactional
    @Override
    public void removeProductFromWishlist(Long productId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", productId));
        WishList wishlist = wishlistRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist", "user", principal.getName()));

        wishlist.getProducts().remove(product);
        wishlistRepository.save(wishlist);
    }

    @Override
    public WishListResponse getUserWishlist(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));

        WishList wishlist = wishlistRepository.findByUser(user).orElseGet(() -> {
            WishList newWishlist = new WishList();
            newWishlist.setUser(user);
            return newWishlist;
        });

        return modelMapper.map(wishlist, WishListResponse.class);
    }
}