package com.bangvan.controller;

import com.bangvan.dto.request.category.DealRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.DealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deals") // Endpoint mới chuyên cho Deals
@RequiredArgsConstructor
@Tag(name = "Deal Management", description = "APIs for Admin to manage promotional deals")
@PreAuthorize("hasRole('ADMIN')") // Bảo vệ toàn bộ controller cho Admin
public class DealController {

    private final DealService dealService;

    @PostMapping
    @Operation(summary = "Create a new Deal", description = "Endpoint for admins to create a new promotional deal.")
    public ResponseEntity<ApiResponse> createDeal(@Valid @RequestBody DealRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Deal created successfully",
                dealService.createDeal(request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all Deals", description = "Endpoint for admins to retrieve all deals.")
    public ResponseEntity<ApiResponse> getAllDeals() {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Deals fetched successfully",
                dealService.getAllDeals()
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Deal by ID", description = "Endpoint to get a specific deal by its ID.")
    public ResponseEntity<ApiResponse> getDealById(@PathVariable Long id) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Deal found successfully",
                dealService.getDealById(id)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Deal", description = "Endpoint for admins to update an existing deal.")
    public ResponseEntity<ApiResponse> updateDeal(@PathVariable Long id, @Valid @RequestBody DealRequest request) {
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Deal updated successfully",
                dealService.updateDeal(id, request)
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Deal", description = "Endpoint for admins to delete a deal.")
    public ResponseEntity<ApiResponse> deleteDeal(@PathVariable Long id) {
        dealService.deleteDeal(id);
        ApiResponse apiResponse = ApiResponse.success(
                HttpStatus.OK.value(),
                "Deal with ID " + id + " has been deleted successfully",
                null
        );
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }
}