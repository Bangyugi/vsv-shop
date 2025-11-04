package com.bangvan.dto.response.cart;
import com.bangvan.entity.CartItem;
import com.bangvan.entity.User;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private User user;
    private BigDecimal totalPrice;
    private BigDecimal totalSellingPrice;
    private Integer totalItem;
    private BigDecimal discount;
    private String couponCode;
    private List<CartItemResponse> cartItems = new ArrayList<>();
}