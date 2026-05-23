package com.example.StoreManagementDemo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private List<OrderRequestItem> items;
}
