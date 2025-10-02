package com.bangvan.entity;

import com.bangvan.utils.PaymentMethod;
import com.bangvan.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payment_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    BigDecimal amount;

    @Enumerated(EnumType.STRING)
    PaymentStatus status = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;

    @Column(columnDefinition = "TEXT")
    String paymentLink;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "paymentOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Order> orders = new HashSet<>();

}