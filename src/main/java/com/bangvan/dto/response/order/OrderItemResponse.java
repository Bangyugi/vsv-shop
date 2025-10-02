package com.bangvan.dto.response.order;

import com.bangvan.entity.Product;
import com.bangvan.entity.ProductVariant;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    private Long id;
    private Product product;
    private ProductVariant variant;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
}