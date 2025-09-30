package com.bangvan.dto.response.order;

import com.bangvan.entity.Product;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponse {
    private Long id;
    private Product product;
    private String size;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
}