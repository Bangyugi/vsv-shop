package com.bangvan.dto.response.coupon;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CouponResponse {
    private Long id;
    private String code;
    private BigDecimal discountPercentage;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal minOrderValue;
    private Boolean isActive;
}