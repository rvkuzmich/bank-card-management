package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.UserController;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "Admin user management operations")
public class UserControllerImpl implements UserController {

  private final UserServiceImpl userService;

  @Override
  public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
    Page<UserResponse> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(String userId, Role role) {
    UserResponse userResponse = userService.updateUserRole(userId, role);
    return ResponseEntity.ok(ApiResponse.success(userResponse, "User role updated"));
  }

  @Override
  public ResponseEntity<ApiResponse<Void>> disableUser(String userId) {
    userService.disableUser(userId);
    return ResponseEntity.ok(ApiResponse.success(null, "User disabled"));
  }

  @Override
  public ResponseEntity<ApiResponse<Void>> enableUser(String userId) {
    userService.enableUser(userId);
    return ResponseEntity.ok(ApiResponse.success(null, "User enabled"));
  }
}
