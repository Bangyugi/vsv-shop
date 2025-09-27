package com.bangvan.dto.response.product;

import com.bangvan.entity.Category;
import com.bangvan.entity.Review;
import com.bangvan.entity.Seller;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductResponse {
    Long id;

    String title;

    String description;

    BigDecimal price;

    BigDecimal sellingPrice;

    Integer discountPercent;

    Integer quantity;

    String color;

    List<String> images = new ArrayList<>();

    Integer numRatings;

    Seller seller;

    Category category;

    String sizes;

    Boolean inStock;
}
