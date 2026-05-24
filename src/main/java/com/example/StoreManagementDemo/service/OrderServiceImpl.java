package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.request.OrderRequest;
import com.example.StoreManagementDemo.dto.request.OrderRequestItem;
import com.example.StoreManagementDemo.dto.response.OrderResponse;
import com.example.StoreManagementDemo.model.Order;
import com.example.StoreManagementDemo.model.OrderItem;
import com.example.StoreManagementDemo.model.OrderStatus;
import com.example.StoreManagementDemo.model.Product;
import com.example.StoreManagementDemo.model.User;
import com.example.StoreManagementDemo.event.ProductDeletedEvent;
import com.example.StoreManagementDemo.repository.OrderRepository;
import com.example.StoreManagementDemo.repository.ProductRepository;
import com.example.StoreManagementDemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public void createOrder(String username, OrderRequest orderRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedBy(username);
        
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequestItem itemReq : orderRequest.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemReq.getProductId()));

            if (product.getStockQuantity() < itemReq.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName());
            }

            product.setStockQuantity(product.getStockQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setCreatedBy(username);

            order.addOrderItem(orderItem);
            
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(String id, OrderStatus status) {
        String currentUserId = currentUserService.getCurrentUserId().orElse("system");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();

        if (status == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    Product product = null;
                    try {
                        product = item.getProduct();
                        if (product != null) {
                            product.getId();
                        }
                    } catch (jakarta.persistence.EntityNotFoundException e) {
                        // Product was soft-deleted
                    }
                    if (product == null || product.isDeleted()) {
                        continue;
                    }
                    int restoredQuantity = product.getStockQuantity() + item.getQuantity();
                    product.setStockQuantity(restoredQuantity);
                    productRepository.save(product);
                }
            }
        }

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(currentUserId);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserId(user.getId()).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelPendingOrdersByProductId(String productId) {
        List<Order> pendingOrders = orderRepository.findPendingOrdersByProductId(productId);
        for (Order order : pendingOrders) {
            updateOrderStatus(order.getId(), OrderStatus.CANCELLED);
        }
    }

    @EventListener
    @Transactional
    public void handleProductDeletedEvent(ProductDeletedEvent event) {
        cancelPendingOrdersByProductId(event.getProductId());
    }
}
