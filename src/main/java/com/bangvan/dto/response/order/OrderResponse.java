package com.bangvan.dto.response.order;

import com.bangvan.entity.Address;
import com.bangvan.entity.OrderItem;
import com.bangvan.entity.User;
import com.bangvan.utils.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderId;
    private User user;
    private Address shippingAddress;
    private BigDecimal totalPrice;
    private OrderStatus orderStatus;
    private int totalItem;
    private LocalDateTime orderDate;
    private LocalDateTime deliverDate;
    private List<OrderItemResponse> orderItems;
}