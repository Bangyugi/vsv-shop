package com.bangvan.service.impl;

import com.bangvan.dto.request.user.ChangePasswordRequest;
import com.bangvan.dto.request.user.CreateUserRequest;
import com.bangvan.dto.request.user.UpdateProfileRequest;
import com.bangvan.dto.response.PageCustomResponse;

import com.bangvan.dto.response.user.UserResponse;
import com.bangvan.entity.*;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.RoleRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;




    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse createUser(CreateUserRequest request){
        log.info("Creating user based on user request");
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }

        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName(request.getRoleName())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND))));

        log.info("Saving user to database");
        user= userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse updateUser(Long userId, UpdateProfileRequest request){
        log.info("Updating user with ID: {}", userId);

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "userId", userId));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (userRepository.existsByPhoneAndIdNot(request.getPhone(), userId)) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }


        modelMapper.map(request, user);

        log.info("Updating user to database");
        user= userRepository.save(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public String deleteUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "userId", userId));
        userRepository.delete(user);
        return "user with "+ userId +" was deleted successfully";
    }


    @Override
    public UserResponse findUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "userId", userId));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getProfile(Principal principal){
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("user", "userId", principal.getName()));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public PageCustomResponse<UserResponse> findAllUsers(Pageable pageable){
        Page<User> page = userRepository.findAll(pageable);
        return PageCustomResponse.<UserResponse>builder()
                .pageNo(page.getNumber()+1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageContent(page.getContent().stream().map(user -> modelMapper.map(user, UserResponse.class)).toList()).build();
    }

    @Override
    public UserResponse changePassword(Principal principal, ChangePasswordRequest request){
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("user", "userId", principal.getName()));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user=userRepository.save(user);
        return modelMapper.map(user,UserResponse.class);
    }


}
