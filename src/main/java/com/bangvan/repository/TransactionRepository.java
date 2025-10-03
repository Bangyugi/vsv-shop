package com.bangvan.repository;

import com.bangvan.entity.Transaction;
import com.bangvan.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByOrder_User(User user, Pageable pageable);
}
