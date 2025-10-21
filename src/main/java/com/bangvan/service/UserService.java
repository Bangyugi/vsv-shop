package com.bangvan.service;

import com.bangvan.dto.request.user.ChangePasswordRequest;
import com.bangvan.dto.request.user.CreateUserRequest;
import com.bangvan.dto.request.user.UpdateProfileRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.user.UserResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface UserService {
    @Transactional(rollbackFor = Exception.class)
    UserResponse createUser(CreateUserRequest request);

    @Transactional(rollbackFor = Exception.class)
    UserResponse updateUser(Principal principal, UpdateProfileRequest request);

    String deleteUser(Long userId);

    UserResponse findUserById(Long userId);

    UserResponse getProfile(Principal principal);

    PageCustomResponse<UserResponse> findAllUsers(Pageable pageable);

    UserResponse changePassword(Principal principal, ChangePasswordRequest request);
}
