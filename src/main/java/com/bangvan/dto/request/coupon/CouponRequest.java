package com.bangvan.dto.request.coupon;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CouponRequest {

    @NotBlank(message = "Coupon code cannot be blank")
    private String code;

    @NotNull(message = "Discount percentage cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount must be greater than 0")
    @DecimalMax(value = "100.0", message = "Discount must be less than or equal to 100")
    private BigDecimal discountPercentage;

    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be in the present or future")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;

    private BigDecimal minOrderValue;

    private Boolean isActive;
}