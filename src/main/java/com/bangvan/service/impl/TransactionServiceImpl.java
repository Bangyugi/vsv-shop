package com.bangvan.service.impl;


import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.order.OrderItemResponse;
import com.bangvan.dto.response.order.OrderResponse;

import com.bangvan.dto.response.payment.TransactionResponse;
import com.bangvan.entity.Order;
import com.bangvan.entity.Transaction;
import com.bangvan.entity.User;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.TransactionRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = modelMapper.map(order, OrderResponse.class);
        List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
                .map(orderItem -> modelMapper.map(orderItem, OrderItemResponse.class))
                .collect(Collectors.toList());
        orderResponse.setOrderItems(orderItemResponses);
        return orderResponse;
    }

    private TransactionResponse mapTransactionToResponse(Transaction transaction) {
        TransactionResponse response = modelMapper.map(transaction, TransactionResponse.class);
        response.setOrder(mapOrderToOrderResponse(transaction.getOrder()));
        return response;
    }

    @Override
    public PageCustomResponse<TransactionResponse> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactionPage = transactionRepository.findAll(pageable);
        List<TransactionResponse> transactionResponses = transactionPage.getContent().stream()
                .map(this::mapTransactionToResponse)
                .collect(Collectors.toList());

        return PageCustomResponse.<TransactionResponse>builder()
                .pageNo(transactionPage.getNumber() + 1)
                .pageSize(transactionPage.getSize())
                .totalPages(transactionPage.getTotalPages())
                .totalElements(transactionPage.getTotalElements())
                .pageContent(transactionResponses)
                .build();
    }

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "ID", transactionId));
        return mapTransactionToResponse(transaction);
    }

    @Override
    public PageCustomResponse<TransactionResponse> getMyTransactions(Principal principal, Pageable pageable) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Page<Transaction> transactionPage = transactionRepository.findByOrder_User(user, pageable);
        List<TransactionResponse> transactionResponses = transactionPage.getContent().stream()
                .map(this::mapTransactionToResponse)
                .collect(Collectors.toList());

        return PageCustomResponse.<TransactionResponse>builder()
                .pageNo(transactionPage.getNumber() + 1)
                .pageSize(transactionPage.getSize())
                .totalPages(transactionPage.getTotalPages())
                .totalElements(transactionPage.getTotalElements())
                .pageContent(transactionResponses)
                .build();
    }
}