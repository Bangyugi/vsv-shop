package com.bangvan.repository;

import com.bangvan.entity.Order;
import com.bangvan.entity.Seller;
import com.bangvan.entity.User;
import com.bangvan.utils.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findByUserAndOrderStatus(User user, OrderStatus orderStatus);

    Page<Order> findBySeller(Seller seller, Pageable pageable);
}
