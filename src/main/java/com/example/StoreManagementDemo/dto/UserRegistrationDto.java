package com.example.StoreManagementDemo.dto;

import com.example.StoreManagementDemo.model.Role;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String password;
    private Role role; // Usually we would force this to USER, but for testing we allow setting it
}
