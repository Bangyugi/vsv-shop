package com.bangvan.dto.request.coupon;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyCouponRequest {
    @NotBlank(message = "Coupon code cannot be blank")
    private String couponCode;
}