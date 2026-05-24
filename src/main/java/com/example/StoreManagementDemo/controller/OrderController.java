package com.example.StoreManagementDemo.controller;

import com.example.StoreManagementDemo.dto.request.OrderRequest;
import com.example.StoreManagementDemo.dto.request.UpdateOrderStatusRequest;
import com.example.StoreManagementDemo.dto.response.OrderResponse;
import com.example.StoreManagementDemo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderRequest orderRequest, Principal principal) {
        orderService.createOrder(principal.getName(), orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Principal principal) {
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
