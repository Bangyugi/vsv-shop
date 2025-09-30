package com.bangvan.service;

import com.bangvan.dto.request.order.CreateOrderRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.order.OrderResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface OrderService {
    @Transactional
    OrderResponse createOrder(CreateOrderRequest request, Principal principal);

    List<OrderResponse> findOrderByUser(Principal principal);

    PageCustomResponse<OrderResponse> findUserOrderHistory(Principal principal, Pageable pageable);

    PageCustomResponse<OrderResponse> getSellerOrders(Principal principal, Pageable pageable);

    @Transactional
    OrderResponse updateOrderStatus(Long orderId, String status, Principal principal);

    @Transactional
    String deleteOrder(Long orderId);

    @Transactional
    OrderResponse cancelOrder(Long orderId, Principal principal);
}
