package com.bangvan.dto.response.seller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerReportResponse {
    private Long id;
    private Long sellerId;
    private BigDecimal totalEarnings;
    private BigDecimal totalSales;
    private BigDecimal totalRefunds;
    private BigDecimal totalTax;
    private BigDecimal netEarnings;
    private Integer totalOrders;
    private Integer canceledOrders;
    private Integer totalTransactions;
}
