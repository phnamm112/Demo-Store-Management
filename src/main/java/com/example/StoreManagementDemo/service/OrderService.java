package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.OrderRequest;
import com.example.StoreManagementDemo.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(String username, OrderRequest orderRequest);
    List<Order> getAllOrders();
    List<Order> getUserOrders(String username);
}
