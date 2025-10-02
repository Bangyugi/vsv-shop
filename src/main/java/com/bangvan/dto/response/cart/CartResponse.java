package com.bangvan.dto.response.cart;
import com.bangvan.entity.CartItem;
import com.bangvan.entity.User;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private User user;
    private BigDecimal totalSellingPrice;
    private Integer totalItem;
    private BigDecimal discount;
    private String couponCode;
    private Set<CartItemResponse> cartItems;
}