package com.bangvan.dto.response.cart;

import com.bangvan.entity.Cart;
import com.bangvan.entity.Product;
import com.bangvan.entity.ProductVariant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private ProductVariant variant;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
}
