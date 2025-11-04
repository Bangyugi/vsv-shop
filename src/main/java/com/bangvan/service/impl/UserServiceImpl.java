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
import com.bangvan.repository.CartRepository;
import com.bangvan.repository.RoleRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.UserService;
import com.bangvan.utils.AccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
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
    private final CartRepository cartRepository;


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
        user.setEnabled(true);
        user= userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);


        log.info("Saving user to database");
        return modelMapper.map(user, UserResponse.class);
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserResponse updateUser(Principal principal, UpdateProfileRequest request){
        String currentUsername = principal.getName();
        log.info("Updating profile for user: {}", currentUsername);

        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        if (request.getEmail() != null && !request.getEmail().equals(currentUser.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        if (request.getPhone() != null && !request.getPhone().equals(currentUser.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        modelMapper.map(request, currentUser);

        log.info("Saving updated user to database");
        User updatedUser = userRepository.save(currentUser);
        return modelMapper.map(updatedUser, UserResponse.class);
    }
    @Override
    public String deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user", "userId", userId));
        user.setAccountStatus(AccountStatus.BANNED);
        user.setEnabled(false);

        userRepository.save(user);

        log.info("User with ID {} was banned (soft deleted).", userId);
        return "User with ID "+ userId +" was banned (soft deleted) successfully";
    }

    @Override
    public UserResponse findUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user", "userId", userId));
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getProfile(Principal principal){
        User user = userRepository.findByUsernameAndEnabledIsTrue(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("user", "userId", principal.getName()));
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
        User user = userRepository.findByUsernameAndEnabledIsTrue(principal.getName()).orElseThrow(() -> new ResourceNotFoundException("user", "userId", principal.getName()));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user=userRepository.save(user);
        return modelMapper.map(user,UserResponse.class);
    }


}
