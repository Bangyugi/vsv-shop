package com.bangvan.entity;

import com.bangvan.utils.OrderStatus;
import com.bangvan.utils.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String orderId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    Seller seller;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderItem> orderItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "shipping_address_id")
    Address shippingAddress;

    BigDecimal totalPrice;

//    @Embedded
//    PaymentDetails paymentDetails = new PaymentDetails();

//
//    BigDecimal totalSellingPrice;

//    Integer discountPercent;

    @Enumerated(EnumType.STRING)
    OrderStatus orderStatus;

    Integer totalItem;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus = PaymentStatus.PENDING;

    LocalDateTime orderDate = LocalDateTime.now();

    LocalDateTime deliverDate = orderDate.plusDays(7);

    @ManyToOne
    @JoinColumn(name = "payment_order_id")
    PaymentOrder paymentOrder;

}