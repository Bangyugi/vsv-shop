package com.bangvan.service;

import com.bangvan.dto.request.seller.BecomeSellerRequest;
import com.bangvan.dto.response.seller.SellerResponse;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

public interface SellerService {
    @Transactional
    SellerResponse becomeSeller(BecomeSellerRequest request, Principal principal);
//    Seller getSellerProfile(String jwt) throws SellerException;
//    Seller createSeller(Seller seller) throws SellerException;
//    Seller getSellerById(Long id) throws SellerException;
//    Seller getSellerByEmail(String email) throws SellerException;
//    List<Seller> getAllSellers(AccountStatus status);
//    Seller updateSeller(Long id, Seller seller) throws SellerException;
//    void deleteSeller(Long id) throws SellerException;
//    Seller verifyEmail(String email,String otp) throws SellerException;
//    Seller updateSellerAccountStatus(Long sellerId, AccountStatus status) throws SellerException;
}
