package com.example.StoreManagementDemo.controller;

import com.example.StoreManagementDemo.dto.OrderRequest;
import com.example.StoreManagementDemo.dto.OrderRequestItem;
import com.example.StoreManagementDemo.service.OrderService;
import com.example.StoreManagementDemo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class WebOrderController {

    private final OrderService orderService;
    private final ProductService productService;

    @GetMapping("/orders")
    public String myOrders(Model model, Principal principal) {
        model.addAttribute("orders", orderService.getUserOrders(principal.getName()));
        return "orders";
    }

    @PostMapping("/orders/create")
    public String createOrder(@RequestParam String productId,
                              @RequestParam Integer quantity,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            OrderRequestItem item = new OrderRequestItem();
            item.setProductId(productId);
            item.setQuantity(quantity);

            List<OrderRequestItem> items = new ArrayList<>();
            items.add(item);

            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setItems(items);

            orderService.createOrder(principal.getName(), orderRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/web/products";
    }
}
