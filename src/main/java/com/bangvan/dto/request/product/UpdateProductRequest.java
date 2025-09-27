package com.bangvan.dto.request.product;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class UpdateProductRequest {
    String title;
    String description;
    BigDecimal price;
    BigDecimal sellingPrice;
    Integer discountPercent;
    Integer quantity;
    String color;
    List<String> images;
    Long categoryId;
    String sizes;
}