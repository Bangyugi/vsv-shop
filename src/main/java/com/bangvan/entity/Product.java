package com.bangvan.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    String description;
    BigDecimal price;
    BigDecimal sellingPrice;
    Integer discountPercent;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    List<String> images = new ArrayList<>();

    Integer numRatings;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    Seller seller;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ProductVariant> variants = new HashSet<>();


    public int getTotalQuantity() {
        if (variants == null) {
            return 0;
        }
        return variants.stream()
                .mapToInt(variant -> (variant.getQuantity() != null) ? variant.getQuantity() : 0)
                .sum();
    }

    public int getTotalSold() {
        if (variants == null) {
            return 0;
        }
        return variants.stream()
                .mapToInt(variant -> (variant.getSold() != null) ? variant.getSold() : 0)
                .sum();
    }
}