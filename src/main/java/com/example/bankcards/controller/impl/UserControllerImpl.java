package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.UserController;
import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.UserService;
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
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserControllerImpl implements UserController {

  private final UserService userService;

  @Override
  @GetMapping
  public ResponseEntity<ApiResponseDto<Page<UserResponseDto>>> getAllUsers(
      @PageableDefault(size = 20, sort = "createdAt,desc") Pageable pageable) {
    Page<UserResponseDto> users = userService.getAllUsers(pageable);
    return ResponseEntity.ok(ApiResponseDto.success(users, "Users retrieved successfully"));
  }

  @Override
  @PatchMapping("/{userId}/role")
  public ResponseEntity<ApiResponseDto<UserResponseDto>> updateUserRole(
      @PathVariable String userId,
      @RequestParam Role role) {
    UserResponseDto userResponseDto = userService.updateUserRole(userId, role);
    return ResponseEntity.ok(ApiResponseDto.success(userResponseDto, "User role updated"));
  }

  @Override
  @PostMapping("/{userId}/disable")
  public ResponseEntity<ApiResponseDto<Void>> disableUser(@PathVariable String userId) {
    userService.disableUser(userId);
    return ResponseEntity.ok(ApiResponseDto.success(null, "User disabled"));
  }

  @Override
  @PostMapping("/{userId}/enable")
  public ResponseEntity<ApiResponseDto<Void>> enableUser(@PathVariable String userId) {
    userService.enableUser(userId);
    return ResponseEntity.ok(ApiResponseDto.success(null, "User enabled"));
  }
}
