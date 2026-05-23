package com.example.StoreManagementDemo.dto;

import lombok.Data;

@Data
public class OrderRequestItem {
    private String productId;
    private Integer quantity;
}
