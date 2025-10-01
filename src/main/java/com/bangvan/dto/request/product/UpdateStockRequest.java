package com.bangvan.dto.request.product;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStockRequest {
    @NotNull
    private Integer quantity;
}