package com.bangvan.service.impl;

import com.bangvan.dto.request.cart.AddItemToCartRequest;
import com.bangvan.entity.CartItem;
import com.bangvan.entity.Product;
import com.bangvan.repository.CartItemRepository;
import com.bangvan.repository.ProductRepository;
import com.bangvan.service.CartService;

import com.bangvan.dto.response.cart.CartResponse;
import com.bangvan.entity.Cart;
import com.bangvan.entity.User;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.CartRepository;
import com.bangvan.repository.UserRepository;
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
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    @Override
    public CartResponse addItemToCart(Principal principal, AddItemToCartRequest request) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.getProductId()));

        Optional<CartItem> existingCartItemOpt = cartItemRepository.findByCartAndProductAndSize(cart, product, request.getSize());

        if (existingCartItemOpt.isPresent()) {
            CartItem existingCartItem = existingCartItemOpt.get();
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingCartItem);
        } else {

            CartItem newCartItem = new CartItem();
            newCartItem.setProduct(product);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(request.getQuantity());
            newCartItem.setSize(request.getSize());
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
        BigDecimal discountPercentage = discount
                .multiply(new BigDecimal("100"))
                .divide(totalPrice, 2, RoundingMode.HALF_UP);
        return discountPercentage;
    }
}
