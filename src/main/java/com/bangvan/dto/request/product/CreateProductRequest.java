package com.bangvan.dto.request.product;

import com.bangvan.entity.Category;


import com.bangvan.entity.Review;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
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
public class CreateProductRequest {

    String title;

    String description;

    BigDecimal price;

    BigDecimal sellingPrice;

    Integer discountPercent;

    Integer quantity;

    String color;

    List<String> images = new ArrayList<>();

    Integer numRatings;

    Long categoryId;

    String sizes;

}
