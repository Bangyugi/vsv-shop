package com.bangvan.dto.response.seller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSummaryResponse {
    /**
     * Số lượng thông báo chưa đọc.
     */
    private long unreadCount;

    /**
     * Tổng số thông báo.
     */
    private long totalCount;
}