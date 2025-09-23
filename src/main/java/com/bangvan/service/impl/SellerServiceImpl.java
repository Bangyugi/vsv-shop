package com.bangvan.service.impl;

import com.bangvan.dto.request.seller.BecomeSellerRequest;
import com.bangvan.dto.response.seller.SellerResponse;
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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class SellerServiceImpl implements SellerService {


    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public SellerResponse becomeSeller(BecomeSellerRequest request, Principal principal){
        String username = principal.getName();
        User user = userRepository.findByUsernameAndEnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("user", "userId", username));
        if(sellerRepository.existsById(user.getId())){
            throw new AppException(ErrorCode.SELLER_EXISTED);
        }



        Seller seller = modelMapper.map(user, Seller.class);

        seller.setBusinessDetails(request.getBusinessDetails());
        seller.setBankDetails(request.getBankDetails());
        seller.setGstin(request.getGstin());

        Address pickupAddress = request.getPickupAddress();
        pickupAddress.setUser(seller);
        seller.setPickupAddress(pickupAddress);

        Role sellerRole = roleRepository.findByName("ROLE_SELLER").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        seller.getRoles().add(sellerRole);

        seller = sellerRepository.save(seller);

        return modelMapper.map(seller, SellerResponse.class);


    }
}
