package com.example.bankcards.service;

import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  UserResponse registerUser(RegisterRequest request);

  UserResponse getUserProfile(String username);

  Page<UserResponse> getAllUsers(Pageable pageable);

  UserResponse updateUserRole(String userId, Role role);

  void disableUser(String userId);

  void enableUser(String userId);
}
