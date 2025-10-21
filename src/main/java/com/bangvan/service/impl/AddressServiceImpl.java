package com.bangvan.service.impl;

import com.bangvan.dto.request.user.AddressRequest;
import com.bangvan.dto.response.user.AddressResponse;
import com.bangvan.entity.Address;
import com.bangvan.entity.User;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.AddressRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public AddressResponse addAddress(AddressRequest request, Principal principal) {
        User user = getUserFromPrincipal(principal);
        log.info("Adding address for user: {}", user.getUsername());

        Address address = modelMapper.map(request, Address.class);
        address.setUser(user); // Gán địa chỉ này cho người dùng hiện tại

        Address savedAddress = addressRepository.save(address);
        log.info("Address saved with ID: {}", savedAddress.getId());
        return modelMapper.map(savedAddress, AddressResponse.class);
    }

    @Override
    public List<AddressResponse> getMyAddresses(Principal principal) {
        User user = getUserFromPrincipal(principal);
        log.info("Fetching addresses for user: {}", user.getUsername());

        // Lấy tất cả địa chỉ từ User entity (vì đã có @OneToMany)
        List<Address> addresses = user.getAddresses().stream().toList();

        return addresses.stream()
                .map(address -> modelMapper.map(address, AddressResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse getAddressById(Long addressId, Principal principal) {
        User user = getUserFromPrincipal(principal);
        log.info("Fetching address with ID: {} for user: {}", addressId, user.getUsername());

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", addressId));

        // Kiểm tra địa chỉ có thuộc về user đang đăng nhập không
        if (!address.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to access address {} which does not belong to them.", user.getUsername(), addressId);
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return modelMapper.map(address, AddressResponse.class);
    }


    @Transactional
    @Override
    public AddressResponse updateAddress(Long addressId, AddressRequest request, Principal principal) {
        User user = getUserFromPrincipal(principal);
        log.info("Updating address with ID: {} for user: {}", addressId, user.getUsername());

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", addressId));

        // Kiểm tra địa chỉ có thuộc về user đang đăng nhập không
        if (!address.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to update address {} which does not belong to them.", user.getUsername(), addressId);
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // Cập nhật thông tin từ request vào address entity
        modelMapper.map(request, address);
        // Đảm bảo user không bị thay đổi (ModelMapper có thể ghi đè nếu tên trường giống)
        address.setUser(user);

        Address updatedAddress = addressRepository.save(address);
        log.info("Address with ID: {} updated successfully.", updatedAddress.getId());
        return modelMapper.map(updatedAddress, AddressResponse.class);
    }

    @Transactional
    @Override
    public void deleteAddress(Long addressId, Principal principal) {
        User user = getUserFromPrincipal(principal);
        log.info("Deleting address with ID: {} for user: {}", addressId, user.getUsername());

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "ID", addressId));

        // Kiểm tra địa chỉ có thuộc về user đang đăng nhập không
        if (!address.getUser().getId().equals(user.getId())) {
            log.warn("User {} attempted to delete address {} which does not belong to them.", user.getUsername(), addressId);
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // Kiểm tra xem địa chỉ có đang được sử dụng trong đơn hàng nào không (Tùy chọn)
        // Nếu có, bạn có thể không cho xóa hoặc xử lý logic khác
        // Ví dụ: if (orderRepository.existsByShippingAddressId(addressId)) { throw new AppException(...); }

        addressRepository.delete(address);
        log.info("Address with ID: {} deleted successfully.", addressId);
    }

    // Hàm tiện ích để lấy User từ Principal
    private User getUserFromPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
}