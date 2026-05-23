package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.model.User;

import java.util.Optional;

public interface CurrentUserService {
    public Optional<User> getCurrentUser();
    public Optional<String> getCurrentUsername();
    public Optional<String> getCurrentUserId();
    public boolean isAuthenticated();
}
