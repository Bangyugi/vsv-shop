package com.bangvan.service.impl;

import com.bangvan.dto.request.coupon.ApplyCouponRequest;
import com.bangvan.dto.request.coupon.CouponRequest;
import com.bangvan.dto.response.coupon.CouponResponse;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.entity.Cart;
import com.bangvan.entity.Coupon;
import com.bangvan.entity.User;
import com.bangvan.exception.AppException;
import com.bangvan.exception.ErrorCode;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.CartRepository;
import com.bangvan.repository.CouponRepository;
import com.bangvan.repository.UserRepository;
import com.bangvan.service.CartService;
import com.bangvan.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ModelMapper modelMapper;
    private final CartService cartService;


    @Transactional
    @Override
    public CouponResponse createCoupon(CouponRequest request) {
        couponRepository.findByCode(request.getCode()).ifPresent(coupon -> {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS, "Coupon code already exists.");
        });

        Coupon coupon = modelMapper.map(request, Coupon.class);
        Coupon savedCoupon = couponRepository.save(coupon);
        return modelMapper.map(savedCoupon, CouponResponse.class);
    }

    @Transactional
    @Override
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coupon", "ID", id));
        modelMapper.map(request, coupon);
        Coupon updatedCoupon = couponRepository.save(coupon);
        return modelMapper.map(updatedCoupon, CouponResponse.class);
    }

    @Transactional
    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Coupon", "ID", id));
        couponRepository.delete(coupon);
    }

    @Override
    public PageCustomResponse<CouponResponse> getAllCoupons(Pageable pageable) {
        Page<Coupon> couponPage = couponRepository.findAll(pageable);
        return PageCustomResponse.<CouponResponse>builder()
                .pageNo(couponPage.getNumber() + 1)
                .pageSize(couponPage.getSize())
                .totalPages(couponPage.getTotalPages())
                .totalElements(couponPage.getTotalElements())
                .pageContent(couponPage.getContent().stream().map(coupon -> modelMapper.map(coupon, CouponResponse.class)).toList())
                .build();
    }

//    @Transactional
//    @Override
//    public CouponResponse applyCoupon(ApplyCouponRequest request, Principal principal) {
//        User user = userRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "username", principal.getName()));
//        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
//                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", request.getCouponCode()));
//        Cart cart = cartRepository.findByUser(user)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart", "user", principal.getName()));
//
//        if (!coupon.getIsActive() || coupon.getStartDate().isAfter(LocalDate.now()) || coupon.getEndDate().isBefore(LocalDate.now())) {
//            throw new AppException(ErrorCode.INVALID_INPUT, "Coupon is not valid or has expired.");
//        }
//
//        if (coupon.getUsedByUser().contains(user)) {
//            throw new AppException(ErrorCode.INVALID_INPUT, "You have already used this coupon.");
//        }
//
//        if (cart.getTotalSellingPrice().compareTo(coupon.getMinOrderValue()) < 0) {
//            throw new AppException(ErrorCode.INVALID_INPUT, "Minimum order value not met for this coupon.");
//        }
//
//        cart.setCouponCode(coupon.getCode());
//        cartRepository.save(cart);

//        cartService.findCartByUser(principal);

//        coupon.getUsedByUser().add(user);
//        couponRepository.save(coupon);
//
//        return modelMapper.map(coupon, CouponResponse.class);
//    }
}