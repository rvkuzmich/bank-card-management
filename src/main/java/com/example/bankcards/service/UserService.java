package com.example.bankcards.service;

import com.example.bankcards.dto.request.RegisterRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserResponse registerUser(RegisterRequest request) {
    validateUsernameAndEmail(request.getUsername(), request.getEmail());

    User user = User.builder()
        .username(request.getUsername())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .role(request.getRole() != null ? request.getRole() : Role.USER)
        .enabled(true)
        .build();

    user = userRepository.save(user);
    log.info("User registered successfully: {}", user.getUsername());

    return mapToResponse(user);
  }

  public UserResponse getUserProfile(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return mapToResponse(user);
  }

  public Page<UserResponse> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(this::mapToResponse);
  }

  public UserResponse updateUserRole(String userId, Role role) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    user.setRole(role);
    user = userRepository.save(user);

    log.info("User role updated: {} -> {}", user.getUsername(), role);
    return mapToResponse(user);
  }

  public void disableUser(String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    user.setEnabled(false);
    userRepository.save(user);

    log.info("User disabled: {}", user.getUsername());
  }

  public void enableUser(String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    user.setEnabled(true);
    userRepository.save(user);

    log.info("User enabled: {}", user.getUsername());
  }

  private void validateUsernameAndEmail(String username, String email) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username already exists");
    }

    if (userRepository.existsByEmail(email)) {
      throw new IllegalArgumentException("Email already exists");
    }
  }

  private UserResponse mapToResponse(User user) {
    int cardCount = user.getCards() != null ? user.getCards().size() : 0;

    return UserResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .enabled(user.isEnabled())
        .createdAt(user.getCreatedAt())
        .cardCount(cardCount)
        .build();
  }
}
