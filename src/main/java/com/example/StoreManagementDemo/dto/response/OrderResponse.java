package com.example.StoreManagementDemo.dto.response;

import com.example.StoreManagementDemo.model.Order;
import com.example.StoreManagementDemo.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderResponse {
    private String id;
    private String userId;
    private String username;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> orderItems;

    public static OrderResponse fromEntity(Order order) {
        if (order == null) {
            return null;
        }
        
        List<OrderItemResponse> items = order.getOrderItems() != null ?
                order.getOrderItems().stream().map(OrderItemResponse::fromEntity).collect(Collectors.toList()) :
                Collections.emptyList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser() != null ? order.getUser().getId() : null)
                .username(order.getUser() != null ? order.getUser().getUsername() : null)
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .orderItems(items)
                .build();
    }
}
