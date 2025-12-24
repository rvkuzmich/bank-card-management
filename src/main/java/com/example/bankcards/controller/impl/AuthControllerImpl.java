package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.AuthController;
import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.JwtResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.UserService;
import com.example.bankcards.security.JwtService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthControllerImpl implements AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final JwtService jwtService;

  @Override
  @PostMapping("/login")
  public ResponseEntity<ApiResponseDto<JwtResponseDto>> login(
      @Valid @RequestBody LoginRequestDto request) {

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

    return ResponseEntity.ok(ApiResponseDto.success(response, "Login successful"));
  }

  @Override
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<ApiResponseDto<UserResponseDto>> register(
      @Valid @RequestBody RegisterRequestDto request) {

    UserResponseDto userResponseDto = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponseDto.success(userResponseDto, "User registered successfully"));
  }

  @Override
  @GetMapping("/profile")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<ApiResponseDto<UserResponseDto>> getProfile(Principal principal) {
    if (principal == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponseDto.error("Authentication required"));
    }
    UserResponseDto userResponseDto = userService.getUserProfile(principal.getName());
    return ResponseEntity.ok(ApiResponseDto.success(userResponseDto, "Profile retrieved"));
  }
}
