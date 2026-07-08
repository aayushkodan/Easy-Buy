package com.aayush.easybuycartorderservice.repository;

import com.aayush.easybuycartorderservice.dto.response.OrderResponse;
import com.aayush.easybuycartorderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
