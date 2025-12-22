package com.example.bankcards.controller;

import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User Management", description = "Admin user management operations")
public class UserController {

  private final UserService userService;

  @GetMapping
  @Operation(summary = "Get all users", description = "Returns paginated list of all users")
  public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
      @PageableDefault(size = 20) Pageable pageable) {
    Page<UserResponse> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(ApiResponse.success(users));
  }

  @PatchMapping("/{userId}/role")
  @Operation(summary = "Update user role", description = "Change user role (USER/ADMIN)")
  public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
      @PathVariable String userId,
      @RequestParam Role role) {
    UserResponse userResponse = userService.updateUserRole(userId, role);
    return ResponseEntity.ok(ApiResponse.success(userResponse, "User role updated"));
  }

  @PostMapping("/{userId}/disable")
  @Operation(summary = "Disable user", description = "Disable user account")
  public ResponseEntity<ApiResponse<Void>> disableUser(
      @PathVariable String userId) {
    userService.disableUser(userId);
    return ResponseEntity.ok(ApiResponse.success(null, "User disabled"));
  }

  @PostMapping("/{userId}/enable")
  @Operation(summary = "Enable user", description = "Enable user account")
  public ResponseEntity<ApiResponse<Void>> enableUser(
      @PathVariable String userId) {
    userService.enableUser(userId);
    return ResponseEntity.ok(ApiResponse.success(null, "User enabled"));
  }
}
