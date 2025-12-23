package com.example.bankcards.controller.impl;

import static com.example.bankcards.constants.TestConstants.INVALID_TEST_ROLE;
import static com.example.bankcards.constants.TestConstants.INVALID_TEST_USER_ID;
import static com.example.bankcards.constants.TestConstants.SUCCESSFUL_OPERATION_MESSAGE;
import static com.example.bankcards.constants.TestConstants.TEST_ADMIN_ROLE;
import static com.example.bankcards.constants.TestConstants.TEST_FIRSTNAME;
import static com.example.bankcards.constants.TestConstants.TEST_LASTNAME;
import static com.example.bankcards.constants.TestConstants.TEST_PAGE_NUMBER;
import static com.example.bankcards.constants.TestConstants.TEST_PAGE_SIZE;
import static com.example.bankcards.constants.TestConstants.TEST_USERNAME;
import static com.example.bankcards.constants.TestConstants.TEST_USER_EMAIL;
import static com.example.bankcards.constants.TestConstants.TEST_USER_ID;
import static com.example.bankcards.constants.TestConstants.TEST_USER_ROLE;
import static com.example.bankcards.constants.TestConstants.USER_DISABLED_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USER_DISABLE_URI;
import static com.example.bankcards.constants.TestConstants.USER_ENABLE_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USER_ENABLE_URI;
import static com.example.bankcards.constants.TestConstants.USER_NOT_FOUND_MESSAGE;
import static com.example.bankcards.constants.TestConstants.USER_UPDATE_ROLE_URI;
import static com.example.bankcards.constants.TestConstants.USER_URI;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private UserControllerImpl userController;

  private MockMvc mockMvc;
  private UserResponseDto userResponseDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(userController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
        .build();

    userResponseDto = UserResponseDto.builder()
        .id(TEST_USER_ID)
        .username(TEST_USERNAME)
        .email(TEST_USER_EMAIL)
        .firstName(TEST_FIRSTNAME)
        .lastName(TEST_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .createdAt(LocalDateTime.now())
        .cardCount(3)
        .build();
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void getAllUsers_Success() throws Exception {
    List<UserResponseDto> users = Arrays.asList(userResponseDto);
    Page<UserResponseDto> page = new PageImpl<>(users, PageRequest
        .of(TEST_PAGE_NUMBER, TEST_PAGE_SIZE), users.size());

    when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get(USER_URI)
            .param("page", TEST_PAGE_NUMBER.toString())
            .param("size", TEST_PAGE_SIZE.toString())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.message", is(SUCCESSFUL_OPERATION_MESSAGE)))
        .andExpect(jsonPath("$.data.content", hasSize(1)))
        .andExpect(jsonPath("$.data.content[0].id", is(TEST_USER_ID)))
        .andExpect(jsonPath("$.data.content[0].username", is(TEST_USERNAME)))
        .andExpect(jsonPath("$.data.content[0].email", is(TEST_USER_EMAIL)))
        .andExpect(jsonPath("$.data.content[0].role", is(TEST_USER_ROLE)))
        .andExpect(jsonPath("$.data.content[0].enabled", is(true)));

    verify(userService, times(1)).getAllUsers(any(Pageable.class));
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void updateUserRole_Success() throws Exception {
    UserResponseDto updatedUser = UserResponseDto.builder()
        .id(TEST_USER_ID)
        .username(TEST_USERNAME)
        .email(TEST_USER_EMAIL)
        .role(Role.ADMIN)
        .enabled(true)
        .build();

    when(userService.updateUserRole(eq(TEST_USER_ID), eq(Role.ADMIN)))
        .thenReturn(updatedUser);

    mockMvc.perform(patch(USER_UPDATE_ROLE_URI, TEST_USER_ID)
            .param("role", TEST_ADMIN_ROLE)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.message", is("User role updated")))
        .andExpect(jsonPath("$.data.id", is(TEST_USER_ID)))
        .andExpect(jsonPath("$.data.role", is(TEST_ADMIN_ROLE)));

    verify(userService, times(1)).updateUserRole(TEST_USER_ID, Role.ADMIN);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void updateUserRole_InvalidRole_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(patch(USER_UPDATE_ROLE_URI, TEST_USER_ID)
            .param("role", INVALID_TEST_ROLE)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void updateUserRole_UserNotFound_ShouldHandleException() throws Exception {
    when(userService.updateUserRole(eq(TEST_USER_ID), eq(Role.ADMIN)))
        .thenThrow(new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));

    mockMvc.perform(patch(USER_UPDATE_ROLE_URI, TEST_USER_ID)
            .param("role", TEST_ADMIN_ROLE)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.message", is(USER_NOT_FOUND_MESSAGE)));

    verify(userService, times(1)).updateUserRole(TEST_USER_ID, Role.ADMIN);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void disableUser_Success() throws Exception {
    doNothing().when(userService).disableUser(TEST_USER_ID);

    mockMvc.perform(post(USER_DISABLE_URI, TEST_USER_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.message", is(USER_DISABLED_MESSAGE)))
        .andExpect(jsonPath("$.data").doesNotExist());

    verify(userService, times(1)).disableUser(TEST_USER_ID);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void enableUser_Success() throws Exception {
    doNothing().when(userService).enableUser(TEST_USER_ID);

    mockMvc.perform(post(USER_ENABLE_URI, TEST_USER_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)))
        .andExpect(jsonPath("$.message", is(USER_ENABLE_MESSAGE)))
        .andExpect(jsonPath("$.data").doesNotExist());

    verify(userService, times(1)).enableUser(TEST_USER_ID);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void enableUser_UserNotFound_ShouldHandleException() throws Exception {
    doThrow(new UsernameNotFoundException(USER_ENABLE_MESSAGE))
        .when(userService).enableUser(TEST_USER_ID);

    mockMvc.perform(post(USER_ENABLE_URI, TEST_USER_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success", is(false)))
        .andExpect(jsonPath("$.message", is(USER_NOT_FOUND_MESSAGE)));

    verify(userService, times(1)).enableUser(TEST_USER_ID);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void getAllUsers_WithCustomPagination() throws Exception {
    Page<UserResponseDto> page = Page.empty(PageRequest.of(2, 5));
    when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

    mockMvc.perform(get(USER_URI)
            .param("page", "2")
            .param("size", "5")
            .param("sort", "username,desc")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.pageable.pageNumber", is(2)))
        .andExpect(jsonPath("$.data.pageable.pageSize", is(5)))
        .andExpect(jsonPath("$.data.empty", is(true)));
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void getAllUsers_EmptyResult() throws Exception {
    Pageable pageable = PageRequest.of(TEST_PAGE_NUMBER, TEST_PAGE_SIZE);
    Page<UserResponseDto> emptyPage = new PageImpl<>(
        Collections.emptyList(), pageable, 0);

    when(userService.getAllUsers(any(Pageable.class))).thenReturn(emptyPage);

    mockMvc.perform(get(USER_URI)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content", hasSize(0)))
        .andExpect(jsonPath("$.data.empty", is(true)));
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void updateUserRole_WithInvalidUserIdFormat() throws Exception {
    String invalidUserId = INVALID_TEST_USER_ID;

    mockMvc.perform(patch(USER_UPDATE_ROLE_URI, invalidUserId)
            .param("role", TEST_ADMIN_ROLE)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(userService, times(1)).updateUserRole(invalidUserId, Role.ADMIN);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void disableUser_UserAlreadyDisabled_ShouldSucceed() throws Exception {
    doNothing().when(userService).disableUser(TEST_USER_ID);

    mockMvc.perform(post(USER_DISABLE_URI, TEST_USER_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)));

    verify(userService, times(1)).disableUser(TEST_USER_ID);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void enableUser_UserAlreadyEnabled_ShouldSucceed() throws Exception {
    doNothing().when(userService).enableUser(TEST_USER_ID);

    mockMvc.perform(post(USER_ENABLE_URI, TEST_USER_ID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", is(true)));

  verify(userService, times(1)).enableUser(TEST_USER_ID);
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void updateUserRole_ConstraintViolation_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(patch(USER_UPDATE_ROLE_URI, TEST_USER_ID)
            .param("role", "")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(roles = TEST_ADMIN_ROLE)
  void getAllUsers_InvalidPageableParameters_ShouldHandleGracefully() throws Exception {
    Pageable pageable = PageRequest.of(TEST_PAGE_NUMBER, TEST_PAGE_SIZE);
    Page<UserResponseDto> emptyPage = new PageImpl<>(
        Collections.emptyList(), pageable, 0);

    when(userService.getAllUsers(any(Pageable.class))).thenReturn(emptyPage);

    mockMvc.perform(get(USER_URI)
            .param("page", "-1")
            .param("size", "1000")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.content", hasSize(0)))
        .andExpect(jsonPath("$.data.empty", is(true)));
  }
}