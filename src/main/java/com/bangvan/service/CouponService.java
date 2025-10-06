package com.bangvan.service;

import com.bangvan.dto.request.coupon.ApplyCouponRequest;
import com.bangvan.dto.request.coupon.CouponRequest;
import com.bangvan.dto.response.PageCustomResponse;
import com.bangvan.dto.response.coupon.CouponResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

public interface CouponService {
    @Transactional
    CouponResponse createCoupon(CouponRequest request);

    @Transactional
    CouponResponse updateCoupon(Long id, CouponRequest request);

    @Transactional
    void deleteCoupon(Long id);

    PageCustomResponse<CouponResponse> getAllCoupons(Pageable pageable);

//    @Transactional
//    CouponResponse applyCoupon(ApplyCouponRequest request, Principal principal);
}
