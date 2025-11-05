package com.bangvan.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String reviewText;

    @Column(nullable = false)
    BigDecimal rating;

    @ElementCollection
    List<String> productImages;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    OrderItem orderItem;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

}