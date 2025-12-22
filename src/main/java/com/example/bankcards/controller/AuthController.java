package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.JwtResponse;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import com.example.bankcards.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtService jwtService;

  @PostMapping("/login")
  @Operation(summary = "Authenticate user", description = "Returns JWT token")
  public ResponseEntity<ApiResponse<JwtResponse>> login(
      @Valid @RequestBody LoginRequest request) {

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

  @PostMapping("/register")
  @Operation(summary = "Register new user", description = "Creates new user account")
  public ResponseEntity<ApiResponse<UserResponse>> register(
      @Valid @RequestBody RegisterRequest request) {

    UserResponse userResponse = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(userResponse, "User registered successfully"));
  }

  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get user profile", description = "Returns current user profile")
  public ResponseEntity<ApiResponse<UserResponse>> getProfile(Principal principal) {
    UserResponse userResponse = userService.getUserProfile(principal.getName());
    return ResponseEntity.ok(ApiResponse.success(userResponse));
  }
}