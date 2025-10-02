package com.bangvan.service.impl;

import com.bangvan.dto.request.cart.UpdateCartItemRequest;
import com.bangvan.dto.response.cart.CartItemResponse;
import com.bangvan.dto.response.product.ProductResponse;
import com.bangvan.entity.*;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.CartItemRepository;
import com.bangvan.repository.CartRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;

    private CartItemResponse mapCartItemToResponse(CartItem cartItem) {
        CartItemResponse cartItemResponse = modelMapper.map(cartItem, CartItemResponse.class);
        cartItemResponse.setProduct(cartItem.getVariant().getProduct());

        return cartItemResponse;
    }

    @Transactional
    @Override
    public void removeCartItem(Principal principal, Long cartItemId) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "ID", cartItemId));
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        updateCartTotals(cart);
    }


    @Transactional
    @Override
    public CartItemResponse updateCartItem(Principal principal, Long cartItemId, UpdateCartItemRequest request) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "ID", cartItemId));

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        ProductVariant variant = cartItem.getVariant();
        Product product = variant.getProduct();
        int requestedQuantity = request.getQuantity();

        if (requestedQuantity > variant.getQuantity()) {
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                    "Only " + variant.getQuantity() + " items left in stock for this variant.");
        }

        if (requestedQuantity <= 0) {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(requestedQuantity);
            cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(requestedQuantity)));
            cartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(requestedQuantity)));
            cartItemRepository.save(cartItem);
        }

        updateCartTotals(cart);

        return mapCartItemToResponse(cartItem);
    }


    private void updateCartTotals(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscountedPrice = BigDecimal.ZERO;
        int totalItem = 0;

        for (CartItem cartsItem : cart.getCartItems()) {
            totalPrice = totalPrice.add(cartsItem.getPrice());
            totalDiscountedPrice = totalDiscountedPrice.add(cartsItem.getSellingPrice());
            totalItem += cartsItem.getQuantity();
        }

        cart.setTotalSellingPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        cart.setDiscount(calculateDiscountPercentage(totalPrice, totalDiscountedPrice));

        cartRepository.save(cart);
    }

    private BigDecimal calculateDiscountPercentage(BigDecimal totalPrice, BigDecimal totalDiscountedPrice) {

        if (totalPrice == null || totalDiscountedPrice == null ||
                totalPrice.compareTo(BigDecimal.ZERO) <= 0 ||
                totalDiscountedPrice.compareTo(totalPrice) > 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = totalPrice.subtract(totalDiscountedPrice);
        BigDecimal discountPercentage = discount
                .multiply(new BigDecimal("100"))
                .divide(totalPrice, 2, RoundingMode.HALF_UP);
        return discountPercentage;
    }

    @Override
    public CartItemResponse findCartItemById(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "ID", cartItemId));
        return mapCartItemToResponse(cartItem);
    }

}
