package com.example.bankcards.controller;

import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/users")
public interface UserController {

  @GetMapping
  @Operation(summary = "Get all users", description = "Returns paginated list of all users")
  ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
      @PageableDefault(size = 20) Pageable pageable);

  @PatchMapping("/{userId}/role")
  @Operation(summary = "Update user role", description = "Change user role (USER/ADMIN)")
  ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
      @PathVariable String userId,
      @RequestParam Role role);

  @PostMapping("/{userId}/disable")
  @Operation(summary = "Disable user", description = "Disable user account")
  ResponseEntity<ApiResponse<Void>> disableUser(
      @PathVariable String userId);

  @PostMapping("/{userId}/enable")
  @Operation(summary = "Enable user", description = "Enable user account")
  ResponseEntity<ApiResponse<Void>> enableUser(
      @PathVariable String userId);
}
