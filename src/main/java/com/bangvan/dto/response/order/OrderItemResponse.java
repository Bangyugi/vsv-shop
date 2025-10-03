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
    String productTitle;
    String variantSku;
    String color;
    String size;
    String imageUrl;
    Integer quantity;
    BigDecimal priceAtPurchase;
    BigDecimal sellingPriceAtPurchase;
}