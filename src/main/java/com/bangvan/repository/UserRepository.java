package com.bangvan.repository;

import com.bangvan.entity.Seller;
import com.bangvan.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByPhone(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


    boolean existsByEmailAndIdNot(String email, Long userId);

    boolean existsByPhoneAndIdNot(String phone, Long userId);

    Optional<User> findByIdAndEnabledIsTrue(Long userId);


    Optional<User> findByUsernameAndEnabledIsTrue(String name);

    Page<User> findByEnabledIsTrue(Pageable pageable);



}
