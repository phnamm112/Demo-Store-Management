package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.request.ProductRequest;
import com.example.StoreManagementDemo.dto.response.ProductResponse;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    Optional<ProductResponse> getProductById(String id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(String id, ProductRequest request);
    void deleteProduct(String id);
}
