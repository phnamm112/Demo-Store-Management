package com.example.StoreManagementDemo.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductDeletedEvent {
    private final String productId;
}
