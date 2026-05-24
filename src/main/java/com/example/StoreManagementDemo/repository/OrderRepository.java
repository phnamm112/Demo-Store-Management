package com.example.StoreManagementDemo.repository;

import com.example.StoreManagementDemo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserId(String userId);

    @org.springframework.data.jpa.repository.Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.product.id = :productId AND o.status = 'PENDING'")
    List<Order> findPendingOrdersByProductId(@org.springframework.data.repository.query.Param("productId") String productId);
}
