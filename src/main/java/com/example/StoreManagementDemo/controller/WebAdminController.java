package com.example.StoreManagementDemo.controller;

import com.example.StoreManagementDemo.dto.request.ProductRequest;
import com.example.StoreManagementDemo.dto.response.OrderResponse;
import com.example.StoreManagementDemo.dto.response.ProductResponse;
import com.example.StoreManagementDemo.service.OrderService;
import com.example.StoreManagementDemo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/web/admin")
@RequiredArgsConstructor
public class WebAdminController {

    private final ProductService productService;
    private final OrderService orderService;

    // ---- Product Management ----

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        // Pass a dummy request object or simply leave it for the form binding if needed
        model.addAttribute("product", new ProductRequest());
        model.addAttribute("isEdit", false); // Add flag to signify "Create" mode
        return "admin/product-form";
    }

    @PostMapping("/products/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam BigDecimal price,
                             @RequestParam Integer stockQuantity,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        ProductRequest product = new ProductRequest();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        productService.createProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
        return "redirect:/web/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String showEditProductForm(@PathVariable String id, Model model) {
        ProductResponse product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        model.addAttribute("isEdit", true); // Add flag to signify "Edit/Modify" mode
        return "admin/product-form";
    }

    @PostMapping("/products/edit/{id}")
    public String editProduct(@PathVariable String id,
                              @RequestParam String name,
                              @RequestParam String description,
                              @RequestParam BigDecimal price,
                              @RequestParam Integer stockQuantity,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        ProductRequest updatedProduct = new ProductRequest();
        updatedProduct.setName(name);
        updatedProduct.setDescription(description);
        updatedProduct.setPrice(price);
        updatedProduct.setStockQuantity(stockQuantity);
        productService.updateProduct(id, updatedProduct);
        redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
        return "redirect:/web/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
        return "redirect:/web/admin/products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable String id, Model model) {
        ProductResponse product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("product", product);
        return "admin/product-detail";
    }

    // ---- Order Management ----

    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<OrderResponse> orders = orderService.getAllOrders();

        long totalOrders = orders.size();

        BigDecimal totalRevenue = orders.stream()
                .filter(order -> "COMPLETED".equals(order.getStatus().name()))
                .map(OrderResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("orders", orders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatusFromAdmin(@PathVariable String id,
                                             @RequestParam String status,
                                             RedirectAttributes redirectAttributes) {
        orderService.updateOrderStatus(id, com.example.StoreManagementDemo.model.OrderStatus.valueOf(status));

        String action = status.equals("COMPLETED") ? "approved" : "cancelled";
        redirectAttributes.addFlashAttribute("successMessage", "Order successfully " + action + "!");

        return "redirect:/web/admin/orders";
    }
}
