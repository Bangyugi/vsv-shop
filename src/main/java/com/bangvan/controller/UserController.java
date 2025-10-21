package com.bangvan.controller;


import com.bangvan.dto.request.user.ChangePasswordRequest;
import com.bangvan.dto.request.user.UpdateProfileRequest;
import com.bangvan.dto.request.user.CreateUserRequest;
import com.bangvan.dto.response.ApiResponse;
import com.bangvan.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create User", description = "Create User")
    @PostMapping("/create")

    public ResponseEntity<ApiResponse> createUser(@Valid @RequestBody CreateUserRequest request){
        log.info("Request: {}", request);
        ApiResponse apiResponse = ApiResponse.success(201, "User created successfully", userService.createUser(request));
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Current User Profile", description = "Update the profile of the currently logged-in user.")
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> updateMyProfile(Principal principal, @Valid @RequestBody UpdateProfileRequest request){
        ApiResponse apiResponse = ApiResponse.success(200, "User profile updated successfully", userService.updateUser(principal, request));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Delete User", description = "Delete User")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId){
        ApiResponse apiResponse = ApiResponse.success(200, "User deleted successfully", userService.deleteUser(userId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Find User By Id", description = "Find User By Id")
    @GetMapping("/find/{userId}")
    public ResponseEntity<ApiResponse> findUserById(@PathVariable Long userId){
        ApiResponse apiResponse = ApiResponse.success(200, "User found successfully", userService.findUserById(userId));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Find User's Profile", description = "Find User's Profile")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> findUserProfile(Principal principal){
        ApiResponse apiResponse = ApiResponse.success(200, "User found successfully", userService.getProfile(principal));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @Operation(summary = "Find All Users", description = "Find All Users")
    @GetMapping
    public ResponseEntity<ApiResponse> findAllUsers(
            @RequestParam(value= "pageNo", defaultValue = "1", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt", required = false) String sortBy,
            @RequestParam(value="sortDir", defaultValue = "ASC", required = false) String sortDir
    ){
        Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        ApiResponse apiResponse = ApiResponse.success(200, "Users found successfully", userService.findAllUsers(pageable));
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<ApiResponse> updatePassword(Principal principal, @Valid @RequestBody ChangePasswordRequest request){
        log.info("change username: {} password request: {}",principal.getName(), request);
        ApiResponse apiResponse = ApiResponse.success(200, "Password updated successfully",userService.changePassword(principal, request));
        return new ResponseEntity<>(apiResponse,HttpStatus.OK);
    }




}
