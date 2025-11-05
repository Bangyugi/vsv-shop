package com.bangvan.service.impl;

import com.bangvan.dto.request.cart.AddItemToCartRequest;
import com.bangvan.dto.request.coupon.ApplyCouponRequest;
import com.bangvan.dto.response.cart.CartItemResponse;
import com.bangvan.dto.response.cart.CartResponse;
import com.bangvan.entity.*;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.*;
import com.bangvan.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j 
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final SellerRepository sellerRepository; 

    private CartResponse mapCartToCartResponse(Cart cart) {
        CartResponse cartResponse = modelMapper.map(cart, CartResponse.class);

        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream()
                .sorted(Comparator.comparing(CartItem::getCreatedAt))
                .map(cartItem -> {
                    CartItemResponse cartItemResponse = modelMapper.map(cartItem, CartItemResponse.class);
                    cartItemResponse.setProduct(cartItem.getVariant().getProduct());
                    return cartItemResponse;
                })
                .collect(Collectors.toList());

        cartResponse.setCartItems(cartItemResponses);
        return cartResponse;
    }

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

        
        
        Optional<Seller> sellerOpt = sellerRepository.findById(user.getId());
        if (sellerOpt.isPresent() && sellerOpt.get().getId().equals(product.getSeller().getId())) {
            log.warn("Seller (User ID: {}) attempted to add their own product (Product ID: {}) to cart.", user.getId(), product.getId());
            throw new AppException(ErrorCode.ACCESS_DENIED, "Sellers cannot add their own products to the cart.");
        }
        

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

        return mapCartToCartResponse(cart);
    }

    
    private void updateCartTotals(Cart cart) {
        BigDecimal subTotalPrice = BigDecimal.ZERO; 
        BigDecimal originalTotalPrice = BigDecimal.ZERO; 
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getVariant().getProduct();
            BigDecimal itemSellingPrice = product.getSellingPrice();
            BigDecimal itemOriginalPrice = product.getPrice(); 
            int quantity = cartItem.getQuantity();

            cartItem.setPrice(itemOriginalPrice.multiply(BigDecimal.valueOf(quantity))); 
            cartItem.setSellingPrice(itemSellingPrice.multiply(BigDecimal.valueOf(quantity))); 
            cartItemRepository.save(cartItem);

            subTotalPrice = subTotalPrice.add(cartItem.getSellingPrice());
            originalTotalPrice = originalTotalPrice.add(cartItem.getPrice()); 
            totalItem += quantity;
        }

        cart.setTotalItem(totalItem);
        cart.setTotalPrice(originalTotalPrice); 

        
        if (cart.getCouponCode() != null && !cart.getCouponCode().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCode(cart.getCouponCode());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (coupon.getIsActive() && coupon.getEndDate().isAfter(LocalDate.now().minusDays(1))
                        && subTotalPrice.compareTo(coupon.getMinOrderValue()) >= 0) {

                    BigDecimal discountAmount = subTotalPrice.multiply(coupon.getDiscountPercentage()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    BigDecimal finalPrice = subTotalPrice.subtract(discountAmount);

                    cart.setTotalSellingPrice(finalPrice); 
                    cart.setDiscount(coupon.getDiscountPercentage());
                } else {
                    
                    cart.setCouponCode(null);
                    cart.setDiscount(null);
                    cart.setTotalSellingPrice(subTotalPrice); 
                }
            } else {
                
                cart.setCouponCode(null);
                cart.setDiscount(null);
                cart.setTotalSellingPrice(subTotalPrice);
            }
        } else {
            
            cart.setTotalSellingPrice(subTotalPrice); 
            cart.setDiscount(calculateDiscountPercentage(originalTotalPrice, subTotalPrice)); 
        }

        cartRepository.save(cart);
    }
    

    
    private BigDecimal calculateDiscountPercentage(BigDecimal totalPrice, BigDecimal totalSellingPrice) {
        if (totalPrice == null || totalSellingPrice == null ||
                totalPrice.compareTo(BigDecimal.ZERO) <= 0 ||
                totalSellingPrice.compareTo(totalPrice) > 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount = totalPrice.subtract(totalSellingPrice);
        return discount.multiply(new BigDecimal("100"))
                .divide(totalPrice, 2, RoundingMode.HALF_UP);
    }


    @Override
    public CartResponse findCartByUser(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));

        updateCartTotals(cart);
        return mapCartToCartResponse(cart);
    }

    @Transactional
    @Override
    public CartResponse applyCoupon(ApplyCouponRequest request, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", request.getCouponCode()));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", principal.getName()));

        
        BigDecimal subTotalPrice = cart.getCartItems().stream()
                .map(item -> item.getVariant().getProduct().getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!coupon.getIsActive() || coupon.getStartDate().isAfter(LocalDate.now()) || coupon.getEndDate().isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Coupon is not valid or has expired.");
        }

        if (coupon.getUsedByUser().contains(user)) {
            throw new AppException(ErrorCode.INVALID_INPUT, "You have already used this coupon.");
        }

        if (subTotalPrice.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Minimum order value not met for this coupon.");
        }

        cart.setCouponCode(coupon.getCode());
        updateCartTotals(cart); 

        coupon.getUsedByUser().add(user);
        couponRepository.save(coupon);

        return mapCartToCartResponse(cart);
    }

}