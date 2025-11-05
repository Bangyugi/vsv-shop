package com.bangvan.repository;

import com.bangvan.entity.Order;
import com.bangvan.entity.Seller;
import com.bangvan.entity.User;
import com.bangvan.utils.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);

    Optional<Order> findByOrderId(String orderId);

    Page<Order> findBySeller(Seller seller, Pageable pageable);

    List<Order> findBySeller(Seller seller);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.orderStatus != 'DELIVERED'")
    List<Order> findByUserAndOrderStatusNotDelivered(User user);

    Page<Order> findByUserAndOrderStatus(User user, OrderStatus orderStatus, Pageable pageable);

    List<Order> findAllByUser(User user);
}
