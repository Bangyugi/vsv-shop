package com.bangvan.service.impl;

import com.bangvan.service.SellerReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import com.bangvan.dto.response.seller.SellerReportResponse;
import com.bangvan.entity.Order;
import com.bangvan.entity.Seller;
import com.bangvan.entity.SellerReport;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.OrderRepository;
import com.bangvan.repository.SellerRepository;
import com.bangvan.repository.SellerReportRepository;
import com.bangvan.utils.OrderStatus;
import org.modelmapper.ModelMapper;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerReportServiceImpl implements SellerReportService {

    private final SellerReportRepository sellerReportRepository;
    private final SellerRepository sellerRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    @Override
    public SellerReportResponse getMySellerReport(Principal principal) {
        String username = principal.getName();
        Seller seller = sellerRepository.findByUser_UsernameAndUser_EnabledIsTrue(username)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "username", username));

        return getOrCreateReport(seller.getId());
    }

    @Override
    public SellerReportResponse getReportBySellerId(Long sellerId) {
        return getOrCreateReport(sellerId);
    }

    private SellerReportResponse getOrCreateReport(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "ID", sellerId));

        SellerReport report = sellerReportRepository.findBySellerId(sellerId)
                .orElseGet(() -> createInitialReport(seller));

        SellerReportResponse response = modelMapper.map(report, SellerReportResponse.class);
        response.setSellerId(seller.getId());
        return response;
    }

    private SellerReport createInitialReport(Seller seller) {
        SellerReport newReport = new SellerReport();
        newReport.setSeller(seller);
        return sellerReportRepository.save(newReport);
    }

    @Transactional
    @Override
    public SellerReportResponse generateSellerReport(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "ID", sellerId));

        List<Order> orders = orderRepository.findBySeller(seller);

        SellerReport report = sellerReportRepository.findBySellerId(sellerId)
                .orElseGet(() -> createInitialReport(seller));

        BigDecimal totalSales = BigDecimal.ZERO;
        int totalOrders = 0;
        int canceledOrders = 0;

        for (Order order : orders) {
            if (order.getOrderStatus() == OrderStatus.DELIVERED) {
                totalSales = totalSales.add(order.getTotalPrice());
                totalOrders++;
            } else if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                canceledOrders++;
            }
        }
        report.setTotalSales(totalSales);
        report.setTotalOrders(totalOrders);
        report.setCanceledOrders(canceledOrders);
        report.setNetEarnings(totalSales);
        report.setTotalEarnings(totalSales);

        SellerReport updatedReport = sellerReportRepository.save(report);

        SellerReportResponse response = modelMapper.map(updatedReport, SellerReportResponse.class);
        response.setSellerId(seller.getId());
        return response;
    }



}