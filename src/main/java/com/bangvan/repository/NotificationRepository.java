package com.bangvan.repository;

import com.bangvan.dto.response.seller.NotificationSummaryResponse;
import com.bangvan.entity.Notification;
import com.bangvan.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Lấy tóm tắt (tổng số và số lượng chưa đọc) thông báo cho một seller cụ thể.
     * Sử dụng COALESCE để đảm bảo trả về 0 thay vì null nếu seller chưa có thông báo nào.
     *
     * @param seller Seller cần đếm thông báo.
     * @return NotificationSummaryResponse DTO chứa unreadCount và totalCount.
     */
    @Query("SELECT new com.bangvan.dto.response.seller.NotificationSummaryResponse(" +
            "COALESCE(SUM(CASE WHEN n.isRead = false THEN 1 ELSE 0 END), 0L), " + // unreadCount
            "COALESCE(COUNT(n.id), 0L)) " + // totalCount
            "FROM Notification n WHERE n.seller = :seller")
    NotificationSummaryResponse getSummaryBySeller(@Param("seller") Seller seller);

}