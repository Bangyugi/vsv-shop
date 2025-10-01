package com.bangvan.repository;

import com.bangvan.entity.Cart;
import com.bangvan.entity.CartItem;
import com.bangvan.entity.Product;
import com.bangvan.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndVariant(Cart cart, ProductVariant variant);
}
