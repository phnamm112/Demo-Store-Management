package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.dto.response.UserResponse;

import java.util.Optional;

public interface CurrentUserService {
    public Optional<UserResponse> getCurrentUser();
    public Optional<String> getCurrentUsername();
    public Optional<String> getCurrentUserId();
    public boolean isAuthenticated();
}
