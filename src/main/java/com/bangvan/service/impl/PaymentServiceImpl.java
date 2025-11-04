package com.bangvan.service.impl;

import com.bangvan.dto.response.payment.PaymentLinkResponse;
import com.bangvan.entity.Order;
import com.bangvan.entity.PaymentOrder;
import com.bangvan.entity.SellerReport;
import com.bangvan.entity.Transaction;
import com.bangvan.exception.ResourceNotFoundException;
import com.bangvan.repository.*;
import com.bangvan.service.PaymentService;
import com.bangvan.service.SellerReportService;
import com.bangvan.utils.OrderStatus;
import com.bangvan.utils.PaymentStatus;
import com.bangvan.utils.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final SellerRepository sellerRepository;
    private final SellerReportService sellerReportService;
    private final SellerReportRepository sellerReportRepository;
    private final PaymentOrderRepository paymentOrderRepository;

    @Value("${payment.vnpay.tmnCode}")
    private String tmnCode;

    @Value("${payment.vnpay.secretKey}")
    private String hashSecret;

    @Value("${payment.vnpay.url}")
    private String vnpayUrl;

    @Value("${payment.vnpay.returnUrl}")
    private String returnUrl;

    @Override
    public PaymentLinkResponse createVnpayPaymentLink(Long orderId, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "ID", orderId));

        long amount = order.getTotalPrice().longValue() * 100;
        String vnp_TxnRef = order.getId().toString() + "_" + System.currentTimeMillis();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + order.getOrderId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        String queryUrl = VnpayUtil.getPaymentUrl(vnp_Params, true);
        String vnp_SecureHash = VnpayUtil.hmacSHA512(hashSecret, queryUrl);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = vnpayUrl + "?" + queryUrl;

        PaymentOrder paymentOrder = order.getPaymentOrder();
        if (paymentOrder != null) {
            paymentOrder.setPaymentLink(paymentUrl);
            paymentOrderRepository.save(paymentOrder);
        }

        return new PaymentLinkResponse(paymentUrl);
    }

    @Override
    @Transactional
    public Map<String, String> processVnpayCallback(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String signValue = VnpayUtil.getPaymentUrl(fields, true);
        String vnp_SecureHash_New = VnpayUtil.hmacSHA512(hashSecret, signValue);

        if (vnp_SecureHash.equals(vnp_SecureHash_New)) {
            String orderInfo = request.getParameter("vnp_TxnRef");
            String[] orderInfoParts = orderInfo.split("_");
            Long orderId = Long.parseLong(orderInfoParts[0]);
            String responseCode = request.getParameter("vnp_ResponseCode");

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order", "ID", orderId));

            PaymentOrder paymentOrder = order.getPaymentOrder();

            if ("00".equals(responseCode)) {
                order.setPaymentStatus(PaymentStatus.COMPLETED);
                order.setOrderStatus(OrderStatus.PROCESSING);
                if (paymentOrder != null) {
                    paymentOrder.setStatus(PaymentStatus.COMPLETED);
                }
                Transaction transaction = new Transaction();
                transaction.setOrder(order);
                transactionRepository.save(transaction);
                SellerReport sellerReport = sellerReportRepository.findBySellerId(order.getSeller().getId())
                        .orElse(new SellerReport());
                sellerReport.setSeller(order.getSeller());
                sellerReport.setTotalEarnings(sellerReport.getTotalEarnings().add(order.getTotalPrice()));
                sellerReport.setTotalSales(sellerReport.getTotalSales().add(order.getTotalPrice()));
                sellerReport.setNetEarnings(sellerReport.getNetEarnings().add(order.getTotalPrice()));
                sellerReport.setTotalOrders(sellerReport.getTotalOrders() + 1);
                sellerReportRepository.save(sellerReport);

            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
                if (paymentOrder != null) {
                    paymentOrder.setStatus(PaymentStatus.FAILED);
                }
            }
            if (paymentOrder != null) {
                paymentOrderRepository.save(paymentOrder);
            }
            orderRepository.save(order);
            return Map.of("RspCode", "00", "Message", "success");
        } else {
            return Map.of("RspCode", "97", "Message", "Invalid Signature");
        }
    }
}