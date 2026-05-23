package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.OrderRequest;
import com.example.StoreManagementDemo.dto.OrderRequestItem;
import com.example.StoreManagementDemo.model.Order;
import com.example.StoreManagementDemo.model.OrderItem;
import com.example.StoreManagementDemo.model.OrderStatus;
import com.example.StoreManagementDemo.model.Product;
import com.example.StoreManagementDemo.model.User;
import com.example.StoreManagementDemo.repository.OrderRepository;
import com.example.StoreManagementDemo.repository.ProductRepository;
import com.example.StoreManagementDemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

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

    @Transactional
    public void updateOrderStatus(String id, OrderStatus status) {
        String currentUserId = currentUserService.getCurrentUserId().orElse("system");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();

        if (status == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    if (product == null || product.isDeleted()) {
                        throw new RuntimeException("Product on order item ID " + item.getId() +
                                " was deleted. Skipping stock restoration.");
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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getUserOrders(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUserId(user.getId());
    }
}
