package com.bangvan.dto.response.review;

import com.bangvan.dto.response.order.OrderItemResponse;
import com.bangvan.dto.response.user.UserResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ReviewResponse {
    private Long id;
    private String reviewText;
    private BigDecimal rating;
    private List<String> productImages;
    private UserResponse user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OrderItemResponse orderItem;
}