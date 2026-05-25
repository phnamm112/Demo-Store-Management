package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.request.OrderRequest;
import com.example.StoreManagementDemo.dto.response.OrderResponse;
import com.example.StoreManagementDemo.model.OrderStatus;

import java.util.List;

public interface OrderService {
    void createOrder(String username, OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getUserOrders(String username);
    void updateOrderStatus(String id, OrderStatus status);
    
    void cancelPendingOrdersByProductId(String productId);
}
