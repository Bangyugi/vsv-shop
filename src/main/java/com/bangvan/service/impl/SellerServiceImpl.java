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
import com.bangvan.repository.AddressRepository; // <-- IMPORT MỚI
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
    private final AddressRepository addressRepository; // <-- TIÊM REPOSITORY MỚI


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

        // --- START FIX ---
        // 1. Lấy đối tượng Address (transient/detached) từ request
        Address pickupAddress = request.getPickupAddress();

        // 2. Gán User (managed) cho Address
        pickupAddress.setUser(user);

        // 3. Chủ động lưu (persist hoặc merge) Address trước tiên.
        //    Thao tác này biến pickupAddress thành một managed entity.
        Address managedPickupAddress = addressRepository.save(pickupAddress);

        // 4. Gán managed Address cho Seller
        seller.setPickupAddress(managedPickupAddress);
        // --- END FIX ---


        Role sellerRole = roleRepository.findByName("ROLE_SELLER").orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        user.getRoles().add(sellerRole);
        userRepository.save(user); // Lưu User (để cập nhật role)

        // Lưu Seller. Giờ đây pickupAddress đã là managed entity,
        // nên việc gán vào Seller (không còn cascade persist) là an toàn.
        seller = sellerRepository.save(seller);


        return mapSellerToSellerResponse(seller);
    }


    @Transactional
    @Override
    public SellerResponse updateSeller(UpdateSellerRequest request, Principal principal){
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("seller", "sellerId", username));

        // --- CẬP NHẬT LOGIC UPDATE ĐỊA CHỈ (TƯƠNG TỰ NHƯ KHI TẠO MỚI) ---
        // Lấy địa chỉ từ request
        Address pickupAddress = request.getPickupAddress();
        // Đảm bảo user được gán (quan trọng nếu đây là địa chỉ mới)
        pickupAddress.setUser(seller.getUser());
        // Lưu/Merge địa chỉ trước
        Address managedPickupAddress = addressRepository.save(pickupAddress);

        seller.setBusinessDetails(request.getBusinessDetails());
        seller.setBankDetails(request.getBankDetails());
        // Gán địa chỉ đã được managed
        seller.setPickupAddress(managedPickupAddress);
        seller.setGstin(request.getGstin());
        sellerRepository.save(seller);


        return mapSellerToSellerResponse(seller);
    }

    @Transactional
    @Override
    public String deleteSeller(Principal principal){
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username).orElseThrow(() -> new ResourceNotFoundException("seller", "sellerId", username));

        // Cân nhắc: Xử lý logic dọn dẹp (vd: xóa địa chỉ pickupAddress nếu nó không được dùng ở đâu khác?)
        // Tạm thời chỉ xóa seller

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