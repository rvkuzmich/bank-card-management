package com.example.bankcards.service.impl;

import static com.example.bankcards.constants.TestConstants.EMAIL_ALREADY_EXISTS_MESSAGE;
import static com.example.bankcards.constants.TestConstants.ENCODED_PASSWORD;
import static com.example.bankcards.constants.TestConstants.INVALID_USER_ID;
import static com.example.bankcards.constants.TestConstants.NEW_EMAIL;
import static com.example.bankcards.constants.TestConstants.NEW_USERNAME;
import static com.example.bankcards.constants.TestConstants.NONEXISTENT_USERNAME;
import static com.example.bankcards.constants.TestConstants.PAGE_NUMBER;
import static com.example.bankcards.constants.TestConstants.PAGE_SIZE;
import static com.example.bankcards.constants.TestConstants.USERNAME_USER;
import static com.example.bankcards.constants.TestConstants.USER_EMAIL;
import static com.example.bankcards.constants.TestConstants.USER_EXISTS_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USER_FIRSTNAME;
import static com.example.bankcards.constants.TestConstants.USER_ID;
import static com.example.bankcards.constants.TestConstants.USER_LASTNAME;
import static com.example.bankcards.constants.TestConstants.USER_NOT_FOUND_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USER_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.mapper.Mapper;
import com.example.bankcards.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private Mapper mapper;

  @InjectMocks
  private UserServiceImpl userService;

  private RegisterRequestDto validRegisterRequest;
  private UserResponseDto userResponseDto;
  private User existingUser;

  @BeforeEach
  void setUp() {
    validRegisterRequest = RegisterRequestDto.builder()
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .password(USER_PASSWORD)
        .firstName(USER_FIRSTNAME)
        .lastName(USER_LASTNAME)
        .role(Role.USER)
        .build();

    existingUser = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .password(USER_PASSWORD)
        .firstName(USER_FIRSTNAME)
        .lastName(USER_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .cards(new ArrayList<>())
        .createdAt(LocalDateTime.now())
        .build();

    userResponseDto = UserResponseDto.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .firstName(USER_FIRSTNAME)
        .lastName(USER_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .createdAt(LocalDateTime.now())
        .cardCount(0)
        .build();
  }

  @Test
  void registerUser_WithValidData_ShouldRegisterSuccessfully() {
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(existingUser);
    when(mapper.toUserResponseDto(any(User.class))).thenReturn(userResponseDto);

    UserResponseDto result = userService.registerUser(validRegisterRequest);

    assertNotNull(result);
    assertEquals(USERNAME_USER, result.getUsername());
    assertEquals(USER_EMAIL, result.getEmail());
    assertEquals(Role.USER, result.getRole());
    assertTrue(result.isEnabled());

    verify(userRepository).existsByUsername(USERNAME_USER);
    verify(userRepository).existsByEmail(USER_EMAIL);
    verify(passwordEncoder).encode(validRegisterRequest.getPassword());
    verify(userRepository).save(any(User.class));
    verify(mapper).toUserResponseDto(existingUser);
  }

  @Test
  void registerUser_WithExistingUsername_ShouldThrowException() {
    when(userRepository.existsByUsername(anyString())).thenReturn(true);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.registerUser(validRegisterRequest)
    );

    assertEquals(USER_EXISTS_MESSAGE, exception.getMessage());
    verify(userRepository).existsByUsername(USERNAME_USER);
    verify(userRepository, never()).existsByEmail(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void registerUser_WithExistingEmail_ShouldThrowException() {
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.registerUser(validRegisterRequest)
    );

    assertEquals(EMAIL_ALREADY_EXISTS_MESSAGE, exception.getMessage());
    verify(userRepository).existsByUsername(USERNAME_USER);
    verify(userRepository).existsByEmail(USER_EMAIL);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void registerUser_WithoutRole_ShouldDefaultToUserRole() {
    RegisterRequestDto requestWithoutRole = RegisterRequestDto.builder()
        .username(NEW_USERNAME)
        .email(NEW_EMAIL)
        .password(USER_PASSWORD)
        .build();

    User savedUser = User.builder()
        .id(USER_ID)
        .username(NEW_USERNAME)
        .email(NEW_EMAIL)
        .role(Role.USER)
        .enabled(true)
        .build();

    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(mapper.toUserResponseDto(any(User.class))).thenReturn(userResponseDto);

    userService.registerUser(requestWithoutRole);

    verify(userRepository).save(argThat(user ->
        user.getRole() == Role.USER && user.isEnabled()
    ));
  }

  @Test
  void getUserProfile_WithExistingUsername_ShouldReturnUserProfile() {
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(existingUser));
    when(mapper.toUserResponseDto(existingUser)).thenReturn(userResponseDto);

    UserResponseDto result = userService.getUserProfile(USERNAME_USER);

    assertNotNull(result);
    assertEquals(USERNAME_USER, result.getUsername());
    verify(userRepository).findByUsername(USERNAME_USER);
    verify(mapper).toUserResponseDto(existingUser);
  }

  @Test
  void getUserProfile_WithNonExistingUsername_ShouldThrowException() {
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
        UsernameNotFoundException.class,
        () -> userService.getUserProfile(NONEXISTENT_USERNAME)
    );

    assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
    verify(userRepository).findByUsername(NONEXISTENT_USERNAME);
    verify(mapper, never()).toUserResponseDto(any(User.class));
  }

  @Test
  void getAllUsers_ShouldReturnPagedResults() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

    User user = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .password(ENCODED_PASSWORD)
        .firstName(USER_FIRSTNAME)
        .lastName(USER_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .cards(Collections.emptyList())
        .createdAt(LocalDateTime.now())
        .build();

    List<User> users = List.of(user);
    Page<User> userPage = new PageImpl<>(users, pageable, 1);

    UserResponseDto responseDto = UserResponseDto.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .firstName(USER_FIRSTNAME)
        .lastName(USER_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .cardCount(0)
        .createdAt(user.getCreatedAt())
        .build();

    when(userRepository.findAll(pageable)).thenReturn(userPage);
    when(mapper.toUserResponseDto(user)).thenReturn(responseDto);

    Page<UserResponseDto> result = userService.getAllUsers(pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(1, result.getContent().size());

    UserResponseDto actualDto = result.getContent().get(0);
    assertNotNull(actualDto);
    assertEquals(USERNAME_USER, actualDto.getUsername());
    assertEquals(USER_EMAIL, actualDto.getEmail());
    assertEquals(Role.USER, actualDto.getRole());
    assertTrue(actualDto.isEnabled());
    assertEquals(0, actualDto.getCardCount());

    verify(userRepository).findAll(pageable);
    verify(mapper).toUserResponseDto(user);
  }

  @Test
  void getAllUsers_WithEmptyDatabase_ShouldReturnEmptyPage() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    Page<User> emptyPage = Page.empty();

    when(userRepository.findAll(pageable)).thenReturn(emptyPage);

    Page<UserResponseDto> result = userService.getAllUsers(pageable);

    assertNotNull(result);
    assertEquals(0, result.getTotalElements());
    assertTrue(result.getContent().isEmpty());
    verify(userRepository).findAll(pageable);
    verify(mapper, never()).toUserResponseDto(any(User.class));
  }

  @Test
  void updateUserRole_WithValidUserAndRole_ShouldUpdateSuccessfully() {
    Role newRole = Role.ADMIN;
    User updatedUser = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .role(newRole)
        .build();

    UserResponseDto updatedResponse = UserResponseDto.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .role(newRole)
        .build();

    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);
    when(mapper.toUserResponseDto(any(User.class))).thenReturn(updatedResponse);

    UserResponseDto result = userService.updateUserRole(USER_ID.toString(), newRole);

    assertNotNull(result);
    assertEquals(newRole, result.getRole());
    verify(userRepository).findById(USER_ID);
    verify(userRepository).save(argThat(user ->
        user.getRole() == newRole
    ));
    verify(mapper).toUserResponseDto(updatedUser);
  }

  @Test
  void updateUserRole_WithNonExistingUser_ShouldThrowException() {
    when(userRepository.findById(UUID.fromString(INVALID_USER_ID.toString())))
        .thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
        UsernameNotFoundException.class,
        () -> userService.updateUserRole(INVALID_USER_ID.toString(), Role.ADMIN)
    );

    assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
    verify(userRepository).findById(INVALID_USER_ID);
    verify(userRepository, never()).save(any(User.class));
    verify(mapper, never()).toUserResponseDto(any(User.class));
  }

  @Test
  void disableUser_WithValidUser_ShouldDisableUser() {
    User userToDisable = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .enabled(true)
        .build();

    User disabledUser = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .enabled(false)
        .build();

    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(userToDisable));
    when(userRepository.save(any(User.class))).thenReturn(disabledUser);

    userService.disableUser(USER_ID.toString());

    verify(userRepository).findById(USER_ID);
    verify(userRepository).save(argThat(user ->
        !user.isEnabled()
    ));
  }

  @Test
  void disableUser_WithNonExistingUser_ShouldThrowException() {
    when(userRepository.findById(UUID.fromString(INVALID_USER_ID.toString())))
        .thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
        UsernameNotFoundException.class,
        () -> userService.disableUser(INVALID_USER_ID.toString())
    );

    assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
    verify(userRepository).findById(INVALID_USER_ID);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void enableUser_WithValidUser_ShouldEnableUser() {
    existingUser.setEnabled(false);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(existingUser);

    userService.enableUser(USER_ID.toString());

    verify(userRepository).findById(USER_ID);
    verify(userRepository).save(argThat(user ->
        user.isEnabled()
    ));
  }

  @Test
  void enableUser_WithNonExistingUser_ShouldThrowException() {
    when(userRepository.findById(UUID.fromString(INVALID_USER_ID.toString())))
        .thenReturn(Optional.empty());

    UsernameNotFoundException exception = assertThrows(
        UsernameNotFoundException.class,
        () -> userService.enableUser(INVALID_USER_ID.toString())
    );

    assertEquals(USER_NOT_FOUND_MESSAGE, exception.getMessage());
    verify(userRepository).findById(INVALID_USER_ID);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void enableUser_AlreadyEnabledUser_ShouldStayEnabled() {
    existingUser.setEnabled(true);
    when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(existingUser);

    userService.enableUser(USER_ID.toString());

    verify(userRepository).save(argThat(User::isEnabled));
  }
}
