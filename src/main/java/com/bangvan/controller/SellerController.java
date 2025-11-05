package com.bangvan.controller;

import com.bangvan.dto.request.seller.BecomeSellerRequest;
import com.bangvan.dto.request.seller.UpdateSellerRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.ProductService;
import com.bangvan.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Slf4j
public class SellerController {

    private final SellerService sellerService;
    private final ProductService productService;

    @Operation(summary = "Become Seller", description = "Become Seller API")
    @PostMapping("/become-seller")
    public ResponseEntity<ApiResponse> becomeSeller(@RequestBody BecomeSellerRequest request, Principal principal){
        log.info("Request: {}", request);
        ApiResponse apiResponse = ApiResponse.success(200, "Become seller successfully", sellerService.becomeSeller(request, principal));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Get Seller Profile", description = "Get Seller Profile API")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(Principal principal){
        ApiResponse apiResponse = ApiResponse.success(200, "Get seller profile successfully", sellerService.getProfile(principal));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Get List Seller", description = "Get List Seller API")
    @GetMapping
    public ResponseEntity<ApiResponse> getListSeller(
            @RequestParam(value= "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value="sortDir", defaultValue = "ASC", required = false) String sortDir
    ){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(200, "Get list seller successfully", sellerService.getAllSellers(pageable));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


    @Operation(summary = "Update Seller", description = "Update Seller API")
    @PutMapping("/update/{sellerId}")
    public ResponseEntity<ApiResponse> updateSeller(Principal principal, @Valid @RequestBody UpdateSellerRequest request){
        ApiResponse apiResponse = ApiResponse.success(200, "Update seller successfully", sellerService.updateSeller(request, principal));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Delete Seller", description = "Delete Seller API")
    @DeleteMapping("/delete/{sellerId}")
    public ResponseEntity<ApiResponse> deleteSeller(Principal principal){
        ApiResponse apiResponse = ApiResponse.success(200, "Delete seller successfully", sellerService.deleteSeller(principal));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }


}
