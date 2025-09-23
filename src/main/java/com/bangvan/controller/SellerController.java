package com.bangvan.controller;

import com.bangvan.dto.request.seller.BecomeSellerRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.SellerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
@Slf4j
public class SellerController {

    private final SellerService sellerService;

    @Operation(summary = "Become Seller", description = "Become Seller API")
    @PostMapping("/become-seller")
    public ResponseEntity<ApiResponse> becomeSeller(@RequestBody BecomeSellerRequest request, Principal principal){
        log.info("Request: {}", request);
        ApiResponse apiResponse = ApiResponse.success(200, "Become seller successfully", sellerService.becomeSeller(request, principal));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
