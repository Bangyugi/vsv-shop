package com.bangvan.service.impl;

import com.bangvan.dto.request.cart.AddItemToCartRequest;
import com.bangvan.dto.response.cart.CartResponse;
import com.bangvan.entity.*;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.*;
import com.bangvan.service.CartService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductVariantRepository productVariantRepository; // Sử dụng repository mới
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public CartResponse addItemToCart(Principal principal, AddItemToCartRequest request) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "ID", request.getVariantId()));

        Product product = variant.getProduct();

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCartAndVariant(cart, variant);

        if (existingCartItemOpt.isPresent()) {
            CartItem existingCartItem = existingCartItemOpt.get();
            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();

            if (newQuantity > variant.getQuantity()) {
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                        "Cannot add " + request.getQuantity() + " more items. Only " + (variant.getQuantity() - existingCartItem.getQuantity()) + " left in stock.");
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            existingCartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(newQuantity)));
            cartItemRepository.save(existingCartItem);
        } else {
            if (request.getQuantity() > variant.getQuantity()) {
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                        "Cannot add " + request.getQuantity() + " items. Only " + variant.getQuantity() + " left in stock.");
            }
            CartItem newCartItem = new CartItem();
            newCartItem.setVariant(variant);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(request.getQuantity());
            newCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            newCartItem.setSellingPrice(product.getSellingPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cart.getCartItems().add(newCartItem);
        }

        updateCartTotals(cart);

        return modelMapper.map(cart, CartResponse.class);
    }


    private void updateCartTotals(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalDiscountedPrice = BigDecimal.ZERO;
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            BigDecimal itemPrice = cartItem.getVariant().getProduct().getPrice();
            BigDecimal itemSellingPrice = cartItem.getVariant().getProduct().getSellingPrice();
            int quantity = cartItem.getQuantity();

            cartItem.setPrice(itemPrice.multiply(BigDecimal.valueOf(quantity)));
            cartItem.setSellingPrice(itemSellingPrice.multiply(BigDecimal.valueOf(quantity)));

            totalPrice = totalPrice.add(cartItem.getPrice());
            totalDiscountedPrice = totalDiscountedPrice.add(cartItem.getSellingPrice());
            totalItem += quantity;
        }

        cart.setTotalSellingPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        cart.setDiscount(calculateDiscountPercentage(totalPrice, totalDiscountedPrice));

        cartRepository.save(cart);
    }

    @Override
    public CartResponse findCartByUser(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));

        updateCartTotals(cart);

        return modelMapper.map(cart, CartResponse.class);
    }

    private BigDecimal calculateDiscountPercentage(BigDecimal totalPrice, BigDecimal totalDiscountedPrice) {
        if (totalPrice == null || totalDiscountedPrice == null ||
                totalPrice.compareTo(BigDecimal.ZERO) <= 0 ||
                totalDiscountedPrice.compareTo(totalPrice) > 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = totalPrice.subtract(totalDiscountedPrice);
        return discount.multiply(new BigDecimal("100"))
                .divide(totalPrice, 2, RoundingMode.HALF_UP);
    }
}