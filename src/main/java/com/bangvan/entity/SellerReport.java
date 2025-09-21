package com.bangvan.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Entity
@Table(name = "seller_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SellerReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "seller_id")
    Seller seller;

    BigDecimal totalEarnings = BigDecimal.ZERO;

    BigDecimal totalSales = BigDecimal.ZERO;

    BigDecimal totalRefunds = BigDecimal.ZERO;

    BigDecimal totalTax = BigDecimal.ZERO;

    BigDecimal netEarnings = BigDecimal.ZERO;

    Integer totalOrders = 0;

    Integer canceledOrders = 0;

    Integer totalTransactions = 0;

}