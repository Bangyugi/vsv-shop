package com.bangvan.dto.response.product;

import com.bangvan.entity.Category;
import com.bangvan.entity.ProductVariant;
import com.bangvan.entity.Seller;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private Integer discountPercent;
    private List<String> images;
    private Integer numRatings;
    private Seller seller;
    private Category category;
    private Set<ProductVariant> variants = new HashSet<>();
    private Double averageRating;
    private Integer totalQuantity;
}