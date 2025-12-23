package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
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
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResponseDto registerUser(RegisterRequestDto request) {
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

  @Override
  public UserResponseDto getUserProfile(String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return mapToResponse(user);
  }

  @Override
  public Page<UserResponseDto> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(this::mapToResponse);
  }

  @Override
  public UserResponseDto updateUserRole(String userId, Role role) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    user.setRole(role);
    user = userRepository.save(user);

    log.info("User role updated: {} -> {}", user.getUsername(), role);
    return mapToResponse(user);
  }

  @Override
  public void disableUser(String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    user.setEnabled(false);
    userRepository.save(user);

    log.info("User disabled: {}", user.getUsername());
  }

  @Override
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

  private UserResponseDto mapToResponse(User user) {
    int cardCount = user.getCards() != null ? user.getCards().size() : 0;

    return UserResponseDto.builder()
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
