package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Standard API response")
public class ApiResponseDto<T> {

  @Schema(
      description = "Operation success flag",
      example = "true"
  )
  private boolean success;

  @Schema(
      description = "Message text",
      example = "Operation completed successfully"
  )
  private String message;

  @Schema(
      description = "Response data (may be null in case of error)"
  )
  private T data;

  @Schema(
      description = "Response timestamp",
      example = "2024-01-15T14:30:00"
  )
  private LocalDateTime timestamp;

  public static <T> ApiResponseDto<T> success(T data) {
    return ApiResponseDto.<T>builder()
        .success(true)
        .message("Operation successful")
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static <T> ApiResponseDto<T> success(T data, String message) {
    return ApiResponseDto.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static <T> ApiResponseDto<T> error(String message) {
    return ApiResponseDto.<T>builder()
        .success(false)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
