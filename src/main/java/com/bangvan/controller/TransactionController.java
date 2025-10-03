package com.bangvan.controller;

import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction", description = "Transaction Management API")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all transactions", description = "Endpoint for admins to get all transactions.")
    public ResponseEntity<ApiResponse> getAllTransactions(
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC", required = false) String sortDir) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Transactions fetched successfully",
                transactionService.getAllTransactions(pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Get transaction by ID", description = "Endpoint to get a specific transaction by its ID.")
    public ResponseEntity<ApiResponse> getTransactionById(@PathVariable Long transactionId) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Transaction found successfully",
                transactionService.getTransactionById(transactionId)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/my-transactions")
    @Operation(summary = "Get my transactions", description = "Endpoint for users to get their own transactions.")
    public ResponseEntity<ApiResponse> getMyTransactions(
            Principal principal,
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "date", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC", required = false) String sortDir) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Your transactions fetched successfully",
                transactionService.getMyTransactions(principal, pageable)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}