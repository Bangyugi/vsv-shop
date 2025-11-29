package com.bangvan.controller;

import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "APIs for Customer browsing experience (Public)")
public class CustomerController {


    private final ReviewService reviewService;




}