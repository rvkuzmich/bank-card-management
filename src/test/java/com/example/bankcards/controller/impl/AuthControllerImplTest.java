package com.example.bankcards.controller.impl;

import static com.example.bankcards.constants.TestConstants.ACCESS_DENIED_MESSAGE;
import static com.example.bankcards.constants.TestConstants.AUTHENTICATION_FAILED_MESSAGE;
import static com.example.bankcards.constants.TestConstants.EMAIL_VALIDATION_MESSAGE;
import static com.example.bankcards.constants.TestConstants.FIRSTNAME_VALIDATION_MESSAGE;
import static com.example.bankcards.constants.TestConstants.INVALID_CREDENTIALS_MESSAGE;
import static com.example.bankcards.constants.TestConstants.INVALID_TEST_EMAIL;
import static com.example.bankcards.constants.TestConstants.INVALID_TEST_PASSWORD;
import static com.example.bankcards.constants.TestConstants.INVALID_TEST_USERNAME;
import static com.example.bankcards.constants.TestConstants.LOGIN_URI;
import static com.example.bankcards.constants.TestConstants.NEW_TEST_FIRSTNAME;
import static com.example.bankcards.constants.TestConstants.NEW_TEST_LASTNAME;
import static com.example.bankcards.constants.TestConstants.NEW_TEST_USERNAME;
import static com.example.bankcards.constants.TestConstants.NEW_TEST_USER_EMAIL;
import static com.example.bankcards.constants.TestConstants.PASSWORD_VALIDATION_MESSAGE;
import static com.example.bankcards.constants.TestConstants.PROFILE_URI;
import static com.example.bankcards.constants.TestConstants.REGISTER_URI;
import static com.example.bankcards.constants.TestConstants.SUCCESSFUL_LOGIN_MESSAGE;
import static com.example.bankcards.constants.TestConstants.SUCCESSFUL_OPERATION_MESSAGE;
import static com.example.bankcards.constants.TestConstants.SUCCESSFUL_USER_REGISTRATION_MESSAGE;
import static com.example.bankcards.constants.TestConstants.TEST_FIRSTNAME;
import static com.example.bankcards.constants.TestConstants.TEST_JWT_TOKEN;
import static com.example.bankcards.constants.TestConstants.TEST_LASTNAME;
import static com.example.bankcards.constants.TestConstants.TEST_NONEXISTENT_USERNAME;
import static com.example.bankcards.constants.TestConstants.TEST_PASSWORD;
import static com.example.bankcards.constants.TestConstants.TEST_USERNAME;
import static com.example.bankcards.constants.TestConstants.TEST_USER_EMAIL;
import static com.example.bankcards.constants.TestConstants.TEST_USER_ID;
import static com.example.bankcards.constants.TestConstants.TEST_USER_ROLE;
import static com.example.bankcards.constants.TestConstants.UNEXPECTED_ERROR_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USERNAME_VALIDATION_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USER_EXISTS_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USER_NOT_FOUND_MESSAGE;
import static com.example.bankcards.constants.TestConstants.VALIDATION_FAILED_MESSAGE;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.request.LoginRequestDto;
import com.example.bankcards.dto.request.RegisterRequestDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerImplTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserService userService;

  @Mock
  private JwtService jwtService;

  @InjectMocks
  private AuthControllerImpl authController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private User testUser;
  private LoginRequestDto validLoginRequestDto;
  private RegisterRequestDto validRegisterRequestDto;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    mockMvc = MockMvcBuilders.standaloneSetup(authController)
        .setControllerAdvice(new GlobalExceptionHandler()).build();

    testUser = User.builder()
        .id(TEST_USER_ID)
        .username(TEST_USERNAME)
        .email(TEST_USER_EMAIL)
        .password(TEST_PASSWORD)
        .firstName(TEST_FIRSTNAME)
        .lastName(TEST_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    validLoginRequestDto = new LoginRequestDto();
    validLoginRequestDto.setUsername(TEST_USERNAME);
    validLoginRequestDto.setPassword(TEST_PASSWORD);

    validRegisterRequestDto = new RegisterRequestDto();
    validRegisterRequestDto.setUsername(NEW_TEST_USERNAME);
    validRegisterRequestDto.setEmail(NEW_TEST_USER_EMAIL);
    validRegisterRequestDto.setPassword(TEST_PASSWORD);
    validRegisterRequestDto.setFirstName(NEW_TEST_FIRSTNAME);
    validRegisterRequestDto.setLastName(NEW_TEST_LASTNAME);
    validRegisterRequestDto.setRole(Role.USER);

    SecurityContextHolder.clearContext();
  }

  @Test
  void login_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(jwtService.generateToken(testUser)).thenReturn(TEST_JWT_TOKEN);
    when(jwtService.getExpirationDate(TEST_JWT_TOKEN))
        .thenReturn(LocalDateTime.now().plusHours(2));

    mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validLoginRequestDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(SUCCESSFUL_LOGIN_MESSAGE))
        .andExpect(jsonPath("$.data.token").value(TEST_JWT_TOKEN))
        .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
        .andExpect(jsonPath("$.data.email").value(TEST_USER_EMAIL))
        .andExpect(jsonPath("$.data.role").value(TEST_USER_ROLE))
        .andExpect(jsonPath("$.data.expiresAt").exists());

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, times(1)).generateToken(testUser);
    verify(jwtService, times(1)).getExpirationDate(TEST_JWT_TOKEN);
  }

  @Test
  void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE));

    mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validLoginRequestDto)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(AUTHENTICATION_FAILED_MESSAGE));

    verify(authenticationManager, times(1))
        .authenticate(any(UsernamePasswordAuthenticationToken.class));
    verify(jwtService, never()).generateToken(any());
  }

  @Test
  void login_WithMissingUsername_ShouldReturnBadRequest() throws Exception {
    LoginRequestDto invalidRequest = new LoginRequestDto();
    invalidRequest.setPassword(TEST_PASSWORD);

    mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)));

    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void login_WithShortUsername_ShouldReturnBadRequest() throws Exception {
    LoginRequestDto invalidRequest = new LoginRequestDto();
    invalidRequest.setUsername(INVALID_TEST_USERNAME); // менее 3 символов
    invalidRequest.setPassword(TEST_PASSWORD);

    mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)))
        .andExpect(jsonPath("$.message", containsString(USERNAME_VALIDATION_MESSAGE)));

    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void login_WithShortPassword_ShouldReturnBadRequest() throws Exception {
    LoginRequestDto invalidRequest = new LoginRequestDto();
    invalidRequest.setUsername(TEST_USERNAME);
    invalidRequest.setPassword(INVALID_TEST_PASSWORD);

    mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)))
        .andExpect(jsonPath("$.message", containsString(PASSWORD_VALIDATION_MESSAGE)));

    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void register_WithValidRequest_ShouldReturnCreated() throws Exception {
    UserResponseDto userResponseDto = UserResponseDto.builder()
        .id(TEST_USER_ID)
        .username(NEW_TEST_USERNAME)
        .email(NEW_TEST_USER_EMAIL)
        .firstName(NEW_TEST_FIRSTNAME)
        .lastName(NEW_TEST_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .createdAt(LocalDateTime.now())
        .cardCount(0)
        .build();

    when(userService.registerUser(any(RegisterRequestDto.class))).thenReturn(userResponseDto);

    mockMvc.perform(post(REGISTER_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRegisterRequestDto)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(SUCCESSFUL_USER_REGISTRATION_MESSAGE))
        .andExpect(jsonPath("$.data.id").value(userResponseDto.getId()))
        .andExpect(jsonPath("$.data.username").value(NEW_TEST_USERNAME))
        .andExpect(jsonPath("$.data.email").value(NEW_TEST_USER_EMAIL))
        .andExpect(jsonPath("$.data.firstName").value(NEW_TEST_FIRSTNAME))
        .andExpect(jsonPath("$.data.lastName").value(NEW_TEST_LASTNAME))
        .andExpect(jsonPath("$.data.role").value(TEST_USER_ROLE))
        .andExpect(jsonPath("$.data.enabled").value(true))
        .andExpect(jsonPath("$.data.cardCount").value(0));

    verify(userService, times(1)).registerUser(any(RegisterRequestDto.class));
  }

  @Test
  void register_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
    RegisterRequestDto invalidRequest = new RegisterRequestDto();
    invalidRequest.setUsername(NEW_TEST_USERNAME);
    invalidRequest.setEmail(INVALID_TEST_EMAIL); // невалидный email
    invalidRequest.setPassword(TEST_PASSWORD);

    mockMvc.perform(post(REGISTER_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)))
        .andExpect(jsonPath("$.message", containsString(EMAIL_VALIDATION_MESSAGE)));

    verify(userService, never()).registerUser(any());
  }

  @Test
  void register_WithMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
    RegisterRequestDto invalidRequest = new RegisterRequestDto();

    mockMvc.perform(post(REGISTER_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)));

    verify(userService, never()).registerUser(any());
  }

  @Test
  void register_WithTooLongFirstName_ShouldReturnBadRequest() throws Exception {
    RegisterRequestDto invalidRequest = new RegisterRequestDto();
    invalidRequest.setUsername(NEW_TEST_USERNAME);
    invalidRequest.setEmail(NEW_TEST_USER_EMAIL);
    invalidRequest.setPassword(INVALID_TEST_PASSWORD);
    invalidRequest.setFirstName("A".repeat(51));

    mockMvc.perform(post(REGISTER_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message", containsString(VALIDATION_FAILED_MESSAGE)))
        .andExpect(jsonPath("$.message", containsString(FIRSTNAME_VALIDATION_MESSAGE)));

    verify(userService, never()).registerUser(any());
  }

  @Test
  void getProfile_WithAuthenticatedUser_ShouldReturnUserProfile() throws Exception {
    UserResponseDto userResponseDto = UserResponseDto.builder()
        .id(testUser.getId())
        .username(testUser.getUsername())
        .email(testUser.getEmail())
        .firstName(testUser.getFirstName())
        .lastName(testUser.getLastName())
        .role(testUser.getRole())
        .enabled(true)
        .createdAt(testUser.getCreatedAt())
        .cardCount(5)
        .build();

    when(userService.getUserProfile(TEST_USERNAME)).thenReturn(userResponseDto);

    Principal principal = () -> TEST_USERNAME;

    mockMvc.perform(get(PROFILE_URI)
            .principal(principal))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value(SUCCESSFUL_OPERATION_MESSAGE))
        .andExpect(jsonPath("$.data.id").value(testUser.getId()))
        .andExpect(jsonPath("$.data.username").value(TEST_USERNAME))
        .andExpect(jsonPath("$.data.email").value(TEST_USER_EMAIL))
        .andExpect(jsonPath("$.data.firstName").value(TEST_FIRSTNAME))
        .andExpect(jsonPath("$.data.lastName").value(TEST_LASTNAME))
        .andExpect(jsonPath("$.data.role").value(TEST_USER_ROLE))
        .andExpect(jsonPath("$.data.cardCount").value(5));

    verify(userService, times(1)).getUserProfile(TEST_USERNAME);
  }

  @Test
  void getProfile_WithNonExistentUser_ShouldReturnNotFound() throws Exception {
    when(userService.getUserProfile(TEST_NONEXISTENT_USERNAME))
        .thenThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

    Principal principal = () -> TEST_NONEXISTENT_USERNAME;

    mockMvc.perform(get(PROFILE_URI)
            .principal(principal))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(USER_NOT_FOUND_MESSAGE));

    verify(userService, times(1)).getUserProfile(TEST_NONEXISTENT_USERNAME);
  }

  @Test
  void getProfile_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
    mockMvc.perform(get(PROFILE_URI))
        .andExpect(status().isUnauthorized());

    verify(userService, never()).getUserProfile(anyString());
  }

  @Test
  void login_WithMalformedJson_ShouldReturnBadRequest() throws Exception {
    String malformedJson = "{ \"username\": \"testuser\", \"password\": }";

    mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(malformedJson))
        .andExpect(status().isBadRequest());

    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void register_WithDuplicateUsername_ShouldHandleServiceException() throws Exception {
    when(userService.registerUser(any(RegisterRequestDto.class)))
        .thenThrow(new RuntimeException(USER_EXISTS_MESSAGE));

    mockMvc.perform(post(REGISTER_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRegisterRequestDto)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(UNEXPECTED_ERROR_MESSAGE));

    verify(userService, times(1)).registerUser(any(RegisterRequestDto.class));
  }

  @Test
  void whenAuthenticationException_thenReturnUnauthorized() throws Exception {
    when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE));

    String loginRequest = """
        {
            "username": "testuser",
            "password": "wrongpassword"
        }
        """;

    mockMvc.perform(post(LOGIN_URI)
            .contentType(MediaType.APPLICATION_JSON)
            .content(loginRequest))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(AUTHENTICATION_FAILED_MESSAGE));
  }

  @Test
  void whenAccessDeniedException_thenReturnForbidden() throws Exception {
    when(userService.getUserProfile(any()))
        .thenThrow(new AccessDeniedException(ACCESS_DENIED_MESSAGE));

    mockMvc.perform(get(PROFILE_URI)
            .principal(() -> "user"))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.message").value(ACCESS_DENIED_MESSAGE));
  }
}