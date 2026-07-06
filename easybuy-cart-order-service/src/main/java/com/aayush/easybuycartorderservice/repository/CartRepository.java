package com.aayush.easybuycartorderservice.repository;

import com.aayush.easybuycartorderservice.entity.Cart;
import com.aayush.easybuycartorderservice.entity.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserIdAndStatus(UUID userId, CartStatus status);
}
