package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthController {

  @PostMapping("/login")
  @Operation(
      summary = "Аутентификация пользователя",
      description = "Возвращает JWT токен")
  ResponseEntity<ApiResponse<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto request);

  @PostMapping("/register")
  @Operation(
      summary = "Регистрация нового пользователя",
      description = "Создает новый аккаунт пользователя")
  ResponseEntity<ApiResponse<UserResponseDto>> register(@Valid @RequestBody RegisterRequestDto request);

  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get user profile", description = "Returns current user profile")
  ResponseEntity<ApiResponse<UserResponseDto>> getProfile(Principal principal);
}
