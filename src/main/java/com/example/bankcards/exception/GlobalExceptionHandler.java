package com.example.bankcards.exception;

import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.toList());

    String errorMessage = String.join(", ", errors);
    log.warn("Validation error: {}", errorMessage);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseDto.error("Validation failed: " + errorMessage));
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleUsernameNotFoundException(
      UsernameNotFoundException ex) {
    log.warn("User not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponseDto.error("User not found"));
  }

  @ExceptionHandler(CardNotFoundException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleCardNotFoundException(
      CardNotFoundException ex) {
    log.warn("Card not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponseDto.error("Card not found"));
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleInsufficientBalanceException(
      InsufficientBalanceException ex) {
    log.warn("Insufficient balance: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseDto.error("Insufficient balance"));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleAuthenticationException(
      AuthenticationException ex) {
    log.warn("Authentication failed: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponseDto.error("Authentication failed"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleAccessDeniedException(
      AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponseDto.error("Access denied"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto<Void>> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred: ", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponseDto.error("An unexpected error occurred"));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex) {
    log.warn("Malformed JSON request: {}", ex.getMessage());

    String errorMessage = "Invalid JSON format";
    if (ex.getMessage() != null && ex.getMessage().contains("JSON parse error")) {
      errorMessage = "Invalid request format. Please check your JSON syntax.";
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseDto.error(errorMessage));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
      ConstraintViolationException ex) {

    List<String> errors = ex.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.toList());

    return ResponseEntity.badRequest()
        .body(ErrorResponseDto.builder()
            .success(false)
            .message("Validation failed")
            .errors(errors)
            .build());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {

    log.warn("Method argument type mismatch: {}", ex.getMessage());

    String errorMessage;
    if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
      Class<?> enumType = ex.getRequiredType();
      String[] enumValues = Arrays.stream(enumType.getEnumConstants())
          .map(Object::toString)
          .toArray(String[]::new);

      errorMessage = String.format("Invalid value '%s' for parameter '%s'. " +
              "Allowed values: %s",
          ex.getValue(),
          ex.getName(),
          String.join(", ", enumValues));
    } else {
      errorMessage = String.format("Invalid value '%s' for parameter '%s'",
          ex.getValue(),
          ex.getName());
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseDto.error(errorMessage));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponseDto<Void>> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException ex) {

    log.warn("Missing request parameter: {}", ex.getMessage());

    String errorMessage = String.format("Required parameter '%s' is missing or invalid",
        ex.getParameterName());

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponseDto.error(errorMessage));
  }
}
