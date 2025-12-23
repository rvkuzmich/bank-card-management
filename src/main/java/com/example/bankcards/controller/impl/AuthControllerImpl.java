package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.AuthController;
import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import com.example.bankcards.security.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthControllerImpl implements AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtService jwtService;

  @Override
  public ResponseEntity<ApiResponse<JwtResponseDto>> login(LoginRequestDto request) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getUsername(),
            request.getPassword()
        )
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    User user = (User) authentication.getPrincipal();
    String jwt = jwtService.generateToken(user);
    LocalDateTime expiresAt = jwtService.getExpirationDate(jwt);

    JwtResponseDto response = new JwtResponseDto(
        jwt,
        user.getUsername(),
        user.getEmail(),
        user.getRole(),
        expiresAt
    );

    return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponseDto>> register(RegisterRequestDto request) {

    UserResponseDto userResponseDto = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(userResponseDto, "User registered successfully"));
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponseDto>> getProfile(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("Authentication required"));
    }
    UserResponseDto userResponseDto = userService.getUserProfile(principal.getName());
    return ResponseEntity.ok(ApiResponse.success(userResponseDto));
  }
}