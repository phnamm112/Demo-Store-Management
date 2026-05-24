package com.example.StoreManagementDemo.dto.response;

import com.example.StoreManagementDemo.model.OrderItem;
import lombok.Builder;
import jakarta.persistence.EntityNotFoundException;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemResponse {
    private String id;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;

    public static OrderItemResponse fromEntity(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        String productId = null;
        String productName = null;
        try {
            if (orderItem.getProduct() != null) {
                productId = orderItem.getProduct().getId();
                productName = orderItem.getProduct().getName();
            }
        } catch (jakarta.persistence.EntityNotFoundException e) {
            // Product was deleted; leave fields null
        }
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(productId)
                .productName(productName)
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
