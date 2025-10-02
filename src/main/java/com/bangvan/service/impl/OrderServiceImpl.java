package com.bangvan.service.impl;


import com.bangvan.dto.request.order.CreateOrderRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.order.OrderItemResponse;
import com.bangvan.dto.response.order.OrderResponse;
import com.bangvan.dto.response.product.ProductResponse;
import com.bangvan.entity.*;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.*;
import com.bangvan.service.OrderService;
import com.bangvan.utils.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final SellerRepository sellerRepository;
    private final ProductVariantRepository productVariantRepository;

    private OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(orderItem -> {
                    OrderItemResponse orderItemResponse = modelMapper.map(orderItem, OrderItemResponse.class);
                    orderItemResponse.setProduct(orderItem.getVariant().getProduct());
                    return orderItemResponse;
                })
                .collect(Collectors.toList());
        orderResponse.setOrderItems(orderItemResponses);
        return orderResponse;
    }

    @Transactional
    @Override
    public List<OrderResponse> createOrder(CreateOrderRequest request, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", username));

        if (cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Address shippingAddress;
        if (request.getShippingAddress() != null) {
            Address newAddress = request.getShippingAddress();
            newAddress.setUser(user);
            shippingAddress = addressRepository.save(newAddress);
        } else if (request.getAddressId() != null) {
            shippingAddress = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", request.getAddressId()));
        } else {
            shippingAddress = user.getAddresses().stream().findFirst()
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        }

        Map<Seller, List<CartItem>> itemsBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(cartItem -> cartItem.getVariant().getProduct().getSeller()));

        List<Order> newOrders = new ArrayList<>();

        for (Map.Entry<Seller, List<CartItem>> entry : itemsBySeller.entrySet()) {
            Seller seller = entry.getKey();
            List<CartItem> sellerCartItems = entry.getValue();

            Order order = new Order();
            order.setUser(user);
            order.setSeller(seller);
            order.setShippingAddress(shippingAddress);
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDateTime.now());
            order.setOrderStatus(OrderStatus.PENDING);

            List<OrderItem> orderItems = new ArrayList<>();
            BigDecimal totalPriceForSeller = BigDecimal.ZERO;
            int totalItemForSeller = 0;

            for (CartItem cartItem : sellerCartItems) {
                ProductVariant variant = cartItem.getVariant();
                int requestedQuantity = cartItem.getQuantity();
                if (variant.getQuantity() < requestedQuantity) {
                    throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK,
                            "Not enough stock for SKU " + variant.getSku() + ". Only " + variant.getQuantity() + " left.");
                }

                variant.setQuantity(variant.getQuantity() - requestedQuantity);
                productVariantRepository.save(variant);

                OrderItem orderItem = new OrderItem();
                orderItem.setVariant(variant);
                orderItem.setQuantity(requestedQuantity);
                orderItem.setPrice(cartItem.getPrice());
                orderItem.setSellingPrice(cartItem.getSellingPrice());
                orderItem.setOrder(order);
                orderItems.add(orderItem);

                totalPriceForSeller = totalPriceForSeller.add(cartItem.getSellingPrice());
                totalItemForSeller += requestedQuantity;
            }

            order.setTotalPrice(totalPriceForSeller);
            order.setTotalItem(totalItemForSeller);
            order.setOrderItems(orderItems);

            Order savedOrder = orderRepository.save(order);
            newOrders.add(savedOrder);
        }

        cart.getCartItems().clear();
        cart.setTotalItem(0);
        cart.setTotalSellingPrice(null);
        cartRepository.save(cart);

        return newOrders.stream()
                .map(this::mapOrderToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> findOrderByUser(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Order> orders = orderRepository.findByUserAndOrderStatusNotDelivered(user);
        return orders.stream()
                .map(order -> mapOrderToOrderResponse(order))
                .collect(Collectors.toList());
    }


    @Override
    public PageCustomResponse<OrderResponse> findUserOrderHistory(Principal principal, Pageable pageable) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Page<Order> orderPage = orderRepository.findByUserAndOrderStatus(user, OrderStatus.DELIVERED, pageable);
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(order -> mapOrderToOrderResponse(order))
                .collect(Collectors.toList());
        return PageCustomResponse.<OrderResponse>builder()
                .pageNo(orderPage.getNumber() + 1)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .pageContent(orderResponses)
                .build();
    }

    @Override
    public PageCustomResponse<OrderResponse> getSellerOrders(Principal principal, Pageable pageable) {
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));

        Page<Order> orderPage = orderRepository.findBySeller(seller, pageable);

        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(order -> mapOrderToOrderResponse(order))
                .collect(Collectors.toList());

        return PageCustomResponse.<OrderResponse>builder()
                .pageNo(orderPage.getNumber() + 1)
                .pageSize(orderPage.getSize())
                .totalPages(orderPage.getTotalPages())
                .totalElements(orderPage.getTotalElements())
                .pageContent(orderResponses)
                .build();
    }

    @Transactional
    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "ID", orderId));

        boolean isAdmin = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));

            if (!order.getSeller().getId().equals(seller.getId())) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
        }

        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setOrderStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_INPUT);
        }

        Order updatedOrder = orderRepository.save(order);
        return mapOrderToOrderResponse(updatedOrder);
    }

    @Transactional
    @Override
    public String deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "ID", orderId));
        orderRepository.delete(order);
        return "Order with ID " + orderId + " has been deleted successfully.";
    }

    @Transactional
    @Override
    public OrderResponse cancelOrder(Long orderId, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "ID", orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (order.getOrderStatus() != OrderStatus.PENDING && order.getOrderStatus() != OrderStatus.PLACED) {
            throw new AppException(ErrorCode.ORDER_CANCELLATION_NOT_ALLOWED);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);

        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getVariant();
            variant.setQuantity(variant.getQuantity() + item.getQuantity());
            productVariantRepository.save(variant);
        }

        Order cancelledOrder = orderRepository.save(order);
        return mapOrderToOrderResponse(cancelledOrder);
    }


}