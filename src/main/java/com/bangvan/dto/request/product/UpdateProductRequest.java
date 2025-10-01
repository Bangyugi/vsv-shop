package com.bangvan.dto.request.product;

import com.bangvan.entity.ProductVariant;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private Integer discountPercent;
    private List<String> images;
    private Long categoryId;
    private Set<ProductVariant> variants;
}