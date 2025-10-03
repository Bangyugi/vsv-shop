package com.bangvan.service;

import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.payment.TransactionResponse;
import org.springframework.data.domain.Pageable;

import java.security.Principal;

public interface TransactionService {
    PageCustomResponse<TransactionResponse> getAllTransactions(Pageable pageable);

    TransactionResponse getTransactionById(Long transactionId);

    PageCustomResponse<TransactionResponse> getMyTransactions(Principal principal, Pageable pageable);
}
