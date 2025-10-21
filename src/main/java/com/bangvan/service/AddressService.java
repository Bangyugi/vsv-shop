package com.bangvan.service;

import com.bangvan.dto.request.user.AddressRequest;
import com.bangvan.dto.response.user.AddressResponse;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface AddressService {

    @Transactional
    AddressResponse addAddress(AddressRequest request, Principal principal);

    List<AddressResponse> getMyAddresses(Principal principal);

    AddressResponse getAddressById(Long addressId, Principal principal); // Lấy địa chỉ cụ thể

    @Transactional
    AddressResponse updateAddress(Long addressId, AddressRequest request, Principal principal);

    @Transactional
    void deleteAddress(Long addressId, Principal principal);
}