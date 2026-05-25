package com.example.StoreManagementDemo.controller;

import com.example.StoreManagementDemo.dto.request.OrderRequest;
import com.example.StoreManagementDemo.dto.request.OrderRequestItem;
import com.example.StoreManagementDemo.service.OrderService;
import com.example.StoreManagementDemo.service.ProductService;
import com.example.StoreManagementDemo.controller.WebCartController.CartItem;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String createOrderFromCart(HttpSession session,
                                      Principal principal,
                                      RedirectAttributes redirectAttributes) {
        try {
            // 1. Retrieve the list of active selections saved within the browser session
            @SuppressWarnings("unchecked")
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

            // 2. Safeguard check to ensure user doesn't submit an empty cart state
            if (cart == null || cart.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Your shopping cart is empty!");
                return "redirect:/web/cart";
            }

            // 3. Map all individual items from your Session Cart over to OrderRequestItems
            List<OrderRequestItem> items = new ArrayList<>();
            for (CartItem cartItem : cart) {
                OrderRequestItem orderItem = new OrderRequestItem();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                items.add(orderItem);
            }

            // 4. Populate your composite request model payload
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setItems(items);

            // 5. Invoke service layer implementation to persist to database
            orderService.createOrder(principal.getName(), orderRequest);

            // 6. Clear out the cart items from memory upon a verified transaction completion
            session.removeAttribute("cart");

            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully!");
            return "redirect:/web/orders"; // Route them to historical checkout list page

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to place order: " + e.getMessage());
            return "redirect:/web/cart";
        }
    }
}
