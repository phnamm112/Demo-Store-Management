package com.example.StoreManagementDemo.controller;

import com.example.StoreManagementDemo.model.Product;
import com.example.StoreManagementDemo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class WebProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public String listProducts(Model model, Authentication authentication) {
        model.addAttribute("products", productService.getAllProducts());
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable String id, Model model, Authentication authentication) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "product-detail";
    }
}
