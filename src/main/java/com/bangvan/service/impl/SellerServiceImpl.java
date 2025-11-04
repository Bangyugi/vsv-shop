package com.bangvan.service.impl;

import com.bangvan.dto.request.seller.BecomeSellerRequest;
import com.bangvan.dto.request.seller.UpdateSellerRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.seller.SellerResponse;
import com.bangvan.dto.response.seller.UpdateSellerStatusRequest;
import com.bangvan.dto.response.user.UserResponse;
import com.bangvan.entity.Address;
import com.bangvan.entity.Role;
import com.bangvan.entity.Seller;
import com.bangvan.entity.User;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.RoleRepository;
import com.bangvan.repository.SellerRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.SellerService;
import com.bangvan.utils.AccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerServiceImpl implements SellerService {


    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;


    private SellerResponse mapSellerToSellerResponse(Seller seller) {

        SellerResponse sellerResponse = modelMapper.map(seller, SellerResponse.class);
        UserResponse userResponse = modelMapper.map(seller.getUser(), UserResponse.class);
        sellerResponse.setUser(userResponse);

        return sellerResponse;
    }

    @Override
    public SellerResponse getProfile(Principal principal){
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("seller", "sellerId", username));


        return mapSellerToSellerResponse(seller);
    }

    @Transactional
    @Override
    public SellerResponse becomeSeller(BecomeSellerRequest request, Principal principal){
        String username = principal.getName();
        User user = userRepository.findByUsernameAndEnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("user", "userId", username));
        if(sellerRepository.existsById(user.getId())){
            throw new AppException(ErrorCode.SELLER_EXISTED);
        }

        Seller seller = new Seller();
        seller.setUser(user);

        seller.setBusinessDetails(request.getBusinessDetails());
        seller.setBankDetails(request.getBankDetails());
        seller.setGstin(request.getGstin());

        Address pickupAddress = request.getPickupAddress();
        pickupAddress.setUser(user);
        seller.setPickupAddress(pickupAddress);

        Role sellerRole = roleRepository.findByName("ROLE_SELLER").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.getRoles().add(sellerRole);
        userRepository.save(user);

        seller = sellerRepository.save(seller);


        return mapSellerToSellerResponse(seller);
    }


    @Transactional
    @Override
    public SellerResponse updateSeller(UpdateSellerRequest request, Principal principal){
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("seller", "sellerId", username));
        seller.setBusinessDetails(request.getBusinessDetails());
        seller.setBankDetails(request.getBankDetails());
        seller.setPickupAddress(request.getPickupAddress());
        seller.setGstin(request.getGstin());
        sellerRepository.save(seller);


        return mapSellerToSellerResponse(seller);
    }

    @Transactional
    @Override
    public String deleteSeller(Principal principal){
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("seller", "sellerId", username));
        sellerRepository.delete(seller);
        return "Delete seller successfully";
    }

    @Override
    public PageCustomResponse<SellerResponse> getAllSellers(Pageable pageable){
        Page<Seller> page = sellerRepository.findAll(pageable);
        return PageCustomResponse.<SellerResponse>builder()
                .pageNo(page.getNumber()+1)
                .pageSize(page.getSize())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())

                .pageContent(page.getContent().stream().map(this::mapSellerToSellerResponse).toList()).build();
    }


    @Transactional(readOnly = true)
    @Override
    public SellerResponse findSellerById(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "ID", sellerId));

        return mapSellerToSellerResponse(seller);
    }

    @Transactional
    @Override
    public SellerResponse updateSellerStatus(Long sellerId, UpdateSellerStatusRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "ID", sellerId));

        User user = seller.getUser();

        AccountStatus newStatus;
        try {

            newStatus = AccountStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status value provided: {}", request.getStatus());
            throw new AppException(ErrorCode.INVALID_INPUT, "Invalid status value: " + request.getStatus());
        }


        seller.setAccountStatus(newStatus);
        user.setAccountStatus(newStatus);



        if (newStatus == AccountStatus.ACTIVE) {
            user.setEnabled(true);
        } else {
            user.setEnabled(false);
        }


        userRepository.save(user);
        Seller updatedSeller = sellerRepository.save(seller);

        return mapSellerToSellerResponse(updatedSeller);
    }

}