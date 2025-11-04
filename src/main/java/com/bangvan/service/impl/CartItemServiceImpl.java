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
import com.bangvan.repository.CouponRepository; // <-- IMPORT MỚI
import com.bangvan.repository.UserRepository;
import com.bangvan.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate; // <-- IMPORT MỚI
import java.util.Optional; // <-- IMPORT MỚI

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final CouponRepository couponRepository; // <-- THÊM REPO

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


    // --- START MODIFICATION: Cập nhật updateCartTotals (đồng bộ với CartServiceImpl) ---
    private void updateCartTotals(Cart cart) {
        BigDecimal subTotalPrice = BigDecimal.ZERO; // Giá bán (Selling Price)
        BigDecimal originalTotalPrice = BigDecimal.ZERO; // Giá gốc (Original Price)
        int totalItem = 0;

        for (CartItem cartsItem : cart.getCartItems()) {
            // Đảm bảo item được load (nếu cần, mặc dù trong transaction thường là OK)
            Product product = cartsItem.getVariant().getProduct();

            // Tính toán lại price và sellingPrice của cartItem (phòng trường hợp updateCartItem chưa save)
            BigDecimal itemOriginalPrice = product.getPrice();
            BigDecimal itemSellingPrice = product.getSellingPrice();
            int quantity = cartsItem.getQuantity();

            cartsItem.setPrice(itemOriginalPrice.multiply(BigDecimal.valueOf(quantity)));
            cartsItem.setSellingPrice(itemSellingPrice.multiply(BigDecimal.valueOf(quantity)));
            // cartItemRepository.save(cartsItem); // Không cần save ở đây nếu hàm gọi nó đã save cartItem

            originalTotalPrice = originalTotalPrice.add(cartsItem.getPrice());
            subTotalPrice = subTotalPrice.add(cartsItem.getSellingPrice());
            totalItem += cartsItem.getQuantity();
        }

        cart.setTotalItem(totalItem);
        cart.setTotalPrice(originalTotalPrice); // <-- SET TỔNG GIÁ GỐC

        // Xử lý logic coupon (tính giảm giá dựa trên subTotalPrice)
        if (cart.getCouponCode() != null && !cart.getCouponCode().isEmpty()) {
            Optional<Coupon> couponOpt = couponRepository.findByCode(cart.getCouponCode());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                if (coupon.getIsActive() && coupon.getEndDate().isAfter(LocalDate.now().minusDays(1))
                        && subTotalPrice.compareTo(coupon.getMinOrderValue()) >= 0) {

                    BigDecimal discountAmount = subTotalPrice.multiply(coupon.getDiscountPercentage()).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                    BigDecimal finalPrice = subTotalPrice.subtract(discountAmount);

                    cart.setTotalSellingPrice(finalPrice); // Giá cuối cùng (đã giảm)
                    cart.setDiscount(coupon.getDiscountPercentage());
                } else {
                    cart.setCouponCode(null);
                    cart.setDiscount(null);
                    cart.setTotalSellingPrice(subTotalPrice);
                    cart.setDiscount(calculateDiscountPercentage(originalTotalPrice, subTotalPrice));
                }
            } else {
                cart.setCouponCode(null);
                cart.setDiscount(null);
                cart.setTotalSellingPrice(subTotalPrice);
                cart.setDiscount(calculateDiscountPercentage(originalTotalPrice, subTotalPrice));
            }
        } else {
            cart.setTotalSellingPrice(subTotalPrice);
            cart.setDiscount(calculateDiscountPercentage(originalTotalPrice, subTotalPrice));
        }

        cartRepository.save(cart);
    }
    // --- END MODIFICATION ---

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