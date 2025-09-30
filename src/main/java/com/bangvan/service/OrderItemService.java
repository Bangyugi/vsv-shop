package com.bangvan.service;

import com.bangvan.dto.response.order.OrderItemResponse;

import java.security.Principal;

public interface OrderItemService {
    OrderItemResponse findOrderItemById(Long orderItemId, Principal principal);
}
