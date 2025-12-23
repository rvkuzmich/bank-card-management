package com.example.bankcards.service;

import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
  UserResponseDto registerUser(RegisterRequestDto request);

  UserResponseDto getUserProfile(String username);

  Page<UserResponseDto> getAllUsers(Pageable pageable);

  UserResponseDto updateUserRole(String userId, Role role);

  void disableUser(String userId);

  void enableUser(String userId);
}
