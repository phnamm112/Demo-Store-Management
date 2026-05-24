package com.example.StoreManagementDemo.controller;

import com.example.StoreManagementDemo.dto.response.ProductResponse;
import com.example.StoreManagementDemo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/web/cart")
@RequiredArgsConstructor
public class WebCartController {

    private final ProductService productService;

    // Lightweight inner DTO to keep track of items inside the session cart
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartItem {
        private String productId;
        private String productName;
        private BigDecimal price;
        private Integer quantity;
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = getCartFromSession(session);

        // Calculate the aggregate cart total price
        BigDecimal total = cart.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cart);
        model.addAttribute("cartTotal", total);
        return "cart"; // Points to cart.html
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam String productId,
                            @RequestParam Integer quantity,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        ProductResponse product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<CartItem> cart = getCartFromSession(session);

        // Check if the product already exists inside the active session cart
        boolean itemExists = false;
        for (CartItem item : cart) {
            if (item.getProductId().equals(productId)) {
                int collectiveQuantity = item.getQuantity() + quantity;
                if (collectiveQuantity > product.getStockQuantity()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Cannot select more items than currently in stock!");
                    return "redirect:/web/products";
                }
                item.setQuantity(collectiveQuantity);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            if (quantity > product.getStockQuantity()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot select more items than currently in stock!");
                return "redirect:/web/products";
            }
            cart.add(new CartItem(product.getId(), product.getName(), product.getPrice(), quantity));
        }

        redirectAttributes.addFlashAttribute("successMessage", "Added " + product.getName() + " to your cart!");
        return "redirect:/web/products";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam String productId, HttpSession session) {
        List<CartItem> cart = getCartFromSession(session);
        cart.removeIf(item -> item.getProductId().equals(productId));
        return "redirect:/web/cart";
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCartFromSession(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
}