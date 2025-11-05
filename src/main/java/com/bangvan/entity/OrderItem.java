package com.bangvan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    ProductVariant variant;

    String productTitle;
    String variantSku;
    String color;
    String size;
    String imageUrl;
    Integer quantity;
    BigDecimal priceAtPurchase;
    BigDecimal sellingPriceAtPurchase;

    @Column(name = "is_reviewed", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    Boolean isReviewed = false;

}