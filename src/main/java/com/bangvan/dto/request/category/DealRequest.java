package com.bangvan.dto.request.category;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealRequest {

    @NotNull(message = "Discount percentage cannot be null")
    @Min(value = 1, message = "Discount must be at least 1%")
    @Max(value = 100, message = "Discount must be at most 100%")
    private Integer discount;

    @NotNull(message = "Home Category ID cannot be null")
    private Long homeCategoryId;
}