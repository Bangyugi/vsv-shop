package com.bangvan.repository;

import com.bangvan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByPhone(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


    boolean existsByEmailAndUserIdNot(String email, Long userId);

    boolean existsByPhoneAndUserIdNot(String phone, Long userId);
}
