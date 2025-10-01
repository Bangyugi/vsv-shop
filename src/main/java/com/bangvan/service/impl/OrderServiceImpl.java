package com.bangvan.service.impl;


import com.bangvan.dto.request.order.CreateOrderRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.order.OrderResponse;
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

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final OrderItemRepository orderItemRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;

    @Transactional
    @Override
    public OrderResponse createOrder(CreateOrderRequest request, Principal principal) {
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

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setOrderId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalPrice(cart.getTotalSellingPrice());
        order.setTotalItem(cart.getTotalItem());

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
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
        }

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        cart.getCartItems().clear();
        cart.setTotalItem(0);
        cart.setTotalSellingPrice(null);
        cartRepository.save(cart);

        return modelMapper.map(savedOrder, OrderResponse.class);
    }

    @Override
    public List<OrderResponse> findOrderByUser(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Order> orders = orderRepository.findByUserAndOrderStatusNotDelivered(user);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }


    @Override
    public PageCustomResponse<OrderResponse> findUserOrderHistory(Principal principal, Pageable pageable) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Page<Order> orderPage = orderRepository.findByUserAndOrderStatus(user, OrderStatus.DELIVERED, pageable);
        List<OrderResponse> orderResponses = orderPage.getContent().stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
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
                .map(order -> modelMapper.map(order, OrderResponse.class))
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
        return modelMapper.map(updatedOrder, OrderResponse.class);
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
        return modelMapper.map(cancelledOrder, OrderResponse.class);
    }


}