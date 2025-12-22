package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.AuthController;
import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.JwtResponse;
import com.example.bankcards.dto.response.UserResponse;
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
  public ResponseEntity<ApiResponse<JwtResponse>> login(LoginRequest request) {

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

    JwtResponse response = new JwtResponse(
        jwt,
        user.getUsername(),
        user.getEmail(),
        user.getRole(),
        expiresAt
    );

    return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponse>> register(RegisterRequest request) {

    UserResponse userResponse = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(userResponse, "User registered successfully"));
  }

  @Override
  public ResponseEntity<ApiResponse<UserResponse>> getProfile(Principal principal) {
    UserResponse userResponse = userService.getUserProfile(principal.getName());
    return ResponseEntity.ok(ApiResponse.success(userResponse));
  }
}