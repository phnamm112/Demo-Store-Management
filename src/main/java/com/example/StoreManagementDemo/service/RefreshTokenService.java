package com.example.StoreManagementDemo.service;

import com.example.StoreManagementDemo.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(String username);
    RefreshToken verifyExpiration(RefreshToken token);
    int deleteByUserId(String userId);
}
