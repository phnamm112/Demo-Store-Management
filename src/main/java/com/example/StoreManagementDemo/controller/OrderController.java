package com.example.StoreManagementDemo.controller;

import com.example.StoreManagementDemo.dto.OrderRequest;
import com.example.StoreManagementDemo.dto.UpdateOrderStatusRequest;
import com.example.StoreManagementDemo.model.Order;
import com.example.StoreManagementDemo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest orderRequest, Principal principal) {
        Order order = orderService.createOrder(principal.getName(), orderRequest);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<Order>> getMyOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getUserOrders(principal.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest updateOrderStatusRequest) {
        orderService.updateOrderStatus(id, updateOrderStatusRequest.getStatus());
        return ResponseEntity.noContent().build();
    }
}
