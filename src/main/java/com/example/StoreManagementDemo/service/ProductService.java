package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(String id);
    Product createProduct(Product product);
    Product updateProduct(String id, Product updatedProduct);
    void deleteProduct(String id);
}
