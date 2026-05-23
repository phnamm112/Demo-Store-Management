package com.example.StoreManagementDemo.repository;

import com.example.StoreManagementDemo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findAllByIsDeletedFalse();

    Optional<Product> findByIdAndIsDeletedFalse(String id);
}
