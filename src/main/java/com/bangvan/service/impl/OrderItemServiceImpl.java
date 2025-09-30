package com.bangvan.service.impl;

import com.bangvan.service.OrderItemService;
import org.springframework.stereotype.Service;

import com.bangvan.dto.response.order.OrderItemResponse;
import com.bangvan.entity.OrderItem;
import com.bangvan.entity.User;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.OrderItemRepository;
import com.bangvan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrderItemResponse findOrderItemById(Long orderItemId, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem", "ID", orderItemId));
        if (!orderItem.getOrder().getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return modelMapper.map(orderItem, OrderItemResponse.class);
    }
}