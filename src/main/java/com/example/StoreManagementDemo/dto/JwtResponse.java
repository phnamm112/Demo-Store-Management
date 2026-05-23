package com.example.StoreManagementDemo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private String username;
    private String role;
    
    public JwtResponse(String accessToken, String refreshToken, String username, String role) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }
}
