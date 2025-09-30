package com.bangvan.controller;


import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
@Tag(name = "OrderItem", description = "Order Item Management API")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/{orderItemId}")
    @Operation(summary = "Find an order item by ID", description = "Endpoint for a user to get details of a specific item in their order")
    public ResponseEntity<ApiResponse> findOrderItemById(
            @PathVariable Long orderItemId,
            Principal principal) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Order item found successfully",
                orderItemService.findOrderItemById(orderItemId, principal)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}