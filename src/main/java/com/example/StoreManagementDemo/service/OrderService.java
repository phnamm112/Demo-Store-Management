package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.OrderRequest;
import com.example.StoreManagementDemo.model.Order;
import com.example.StoreManagementDemo.model.OrderStatus;

import java.util.List;

public interface OrderService {
    Order createOrder(String username, OrderRequest orderRequest);
    List<Order> getAllOrders();
    List<Order> getUserOrders(String username);
    void updateOrderStatus(String id, OrderStatus status);
}
