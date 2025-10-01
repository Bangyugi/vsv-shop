package com.bangvan.dto.request.cart;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddItemToCartRequest {
    @NotNull(message = "Product Variant ID cannot be null")
    private Long variantId;

    @NotNull(message = "Quantity cannot be null")
    private Integer quantity;
}
