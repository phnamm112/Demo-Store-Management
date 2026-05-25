package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.request.ProductRequest;
import com.example.StoreManagementDemo.dto.response.ProductResponse;
import com.example.StoreManagementDemo.model.Product;
import com.example.StoreManagementDemo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CurrentUserService currentUserService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAllByIsDeletedFalse().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> getProductById(String id) {
        return productRepository.findByIdAndIsDeletedFalse(id)
                .map(ProductResponse::fromEntity);
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        String currentUserId = currentUserService.getCurrentUserId().orElse("system");
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
        product.setCreatedBy(currentUserId);
        product.setUpdatedBy(currentUserId);
        Product saved = productRepository.save(product);
        return ProductResponse.fromEntity(saved);
    }

    @Override
    public ProductResponse updateProduct(String id, ProductRequest request) {
        String currentUserId = currentUserService.getCurrentUserId().orElse("system");

        return productRepository.findByIdAndIsDeletedFalse(id)
                .map(product -> {
                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPrice(request.getPrice());
                    product.setStockQuantity(request.getStockQuantity());
                    product.setUpdatedAt(java.time.LocalDateTime.now());
                    product.setUpdatedBy(currentUserId);

                    Product saved = productRepository.save(product);
                    return ProductResponse.fromEntity(saved);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public void deleteProduct(String id) {
        String currentUserId = currentUserService.getCurrentUserId().orElse("system");
        productRepository.findByIdAndIsDeletedFalse(id)
                .map(product -> {
                    product.setDeleted(true);
                    product.setDeletedAt(java.time.LocalDateTime.now());
                    product.setDeletedBy(currentUserId);

                    // Publish domain event instead of coupling to OrderService directly
                    eventPublisher.publishEvent(new com.example.StoreManagementDemo.event.ProductDeletedEvent(id));

                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
