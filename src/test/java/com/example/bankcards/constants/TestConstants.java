package com.example.bankcards.constants;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TestConstants {

  public static final String CARDS_URI = "/api/cards";
  public static final String CARDS_ACTIVATE_URI = "/api/cards/{cardId}/activate";
  public static final String CARDS_APPROVE_BLOCK_URI = "/api/cards/{cardId}/approve-block";
  public static final String CARDS_BALANCE_URI = "/api/cards/{cardId}/balance";
  public static final String CARDS_BLOCK_URI = "/api/cards/{cardId}/block";
  public static final String LOGIN_URI = "/api/auth/login";
  public static final String PROFILE_URI = "/api/auth/profile";
  public static final String REGISTER_URI = "/api/auth/register";
  public static final String TRANSFER_HISTORY_URI = "/api/transfers/history";
  public static final String TRANSFER_URI = "/api/transfers";
  public static final String USER_URI = "/api/users";
  public static final String USER_DISABLE_URI = "/api/users/{userId}/disable";
  public static final String USER_ENABLE_URI = "/api/users/{userId}/enable";
  public static final String USER_UPDATE_ROLE_URI = "/api/users/{userId}/role";

  public static final String INVALID_TEST_CARDHOLDER = "J";
  public static final String INVALID_TEST_CARD_NUMBER = "123";
  public static final String INVALID_TEST_USER_ID = "1234";
  public static final String INVALID_TEST_USERNAME = "ab";
  public static final String INVALID_TEST_PASSWORD = "12345";
  public static final String INVALID_TEST_EMAIL = "invalid-email";
  public static final String INVALID_TEST_ROLE = "INVALID_ROLE";
  public static final String NEW_TEST_FIRSTNAME = "Petr";
  public static final String NEW_TEST_LASTNAME = "Petrov";
  public static final String NEW_TEST_USERNAME = "newuser";
  public static final String NEW_TEST_USER_EMAIL = "new@example.com";
  public static final String TEST_CARD_ID = UUID.randomUUID().toString();
  public static final String TEST_CARD_NUMBER = "1234567812345678";
  public static final String TEST_CARDHOLDER = "Ivan Ivanov";
  public static final String TEST_CARD_STATUS_ACTIVE = "ACTIVE";
  public static final String TEST_CARD_STATUS_BLOCKED = "BLOCKED";
  public static final String TEST_DESCRIPTION = "Monthly savings";
  public static final String TEST_FIRSTNAME = "Ivan";
  public static final String TEST_JWT_TOKEN = "test-jwt-token";
  public static final String TEST_LASTNAME = "Ivanov";
  public static final String TEST_MASKED_CARD_NUMBER = "**** **** **** 1234";
  public static final String TEST_NONEXISTENT_USERNAME = "nonexistent";
  public static final String TEST_PASSWORD = "password123";
  public static final String TEST_SOURCE_CARD = UUID.randomUUID().toString();
  public static final String TEST_TARGET_CARD = UUID.randomUUID().toString();
  public static final String TEST_TRANSFER_ID = UUID.randomUUID().toString();
  public static final String TEST_USER_ID = UUID.randomUUID().toString();
  public static final String TEST_ADMIN_ID = UUID.randomUUID().toString();
  public static final String TEST_USER_ROLE = "USER";
  public static final String TEST_ADMIN_ROLE = "ADMIN";
  public static final String TEST_USERNAME = "username";
  public static final String TEST_USERNAME_ADMIN = "admin";
  public static final String TEST_USER_EMAIL = "test@example.com";

  public static final String ACCESS_DENIED_MESSAGE = "Access denied";
  public static final String AMOUNT_IS_REQUIRED_MESSAGE = "Amount is required";
  public static final String AUTHENTICATION_FAILED_MESSAGE = "Authentication failed";
  public static final String BLOCK_REQUEST_MESSAGE = "Block request submitted for approval";
  public static final String CARD_NOT_FOUND_MESSAGE = "Card not found";
  public static final String EMAIL_VALIDATION_MESSAGE = "Email should be valid";
  public static final String FIRSTNAME_VALIDATION_MESSAGE = "First name must not exceed 50 characters";
  public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid credentials";
  public static final String INVALID_JSON_FORMAT_MESSAGE = "Invalid request format. Please check your JSON syntax.";
  public static final String PASSWORD_VALIDATION_MESSAGE = "Password must be between 6 and 100 characters";
  public static final String SUCCESSFUL_LOGIN_MESSAGE = "Login successful";
  public static final String SUCCESSFUL_OPERATION_MESSAGE = "Operation successful";
  public static final String SUCCESSFUL_TRANSFER_MESSAGE = "Transfer completed successfully";
  public static final String SUCCESSFUL_USER_REGISTRATION_MESSAGE = "User registered successfully";
  public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
  public static final String USER_DISABLED_MESSAGE = "User disabled";
  public static final String USER_ENABLE_MESSAGE = "User enabled";
  public static final String USER_EXISTS_MESSAGE = "Username already exists";
  public static final String USER_NOT_FOUND_MESSAGE = "User not found";
  public static final String USERNAME_VALIDATION_MESSAGE = "Username must be between 3 and 50 characters";
  public static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

  public static final LocalDate TEST_EXPIRY_DATE = LocalDate.now().plusYears(3);
  public static final BigDecimal TEST_BALANCE = new BigDecimal("1500.50");
  public static final BigDecimal TEST_MAX_BALANCE = new BigDecimal("5000.00");
  public static final BigDecimal TEST_MIN_BALANCE = new BigDecimal("100.00");
  public static final Integer TEST_PAGE_NUMBER = 0;
  public static final Integer TEST_PAGE_SIZE = 10;

}
