package com.bangvan.dto.response.payment;


import com.bangvan.dto.response.order.OrderResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private OrderResponse order;
    private LocalDateTime date;
}
