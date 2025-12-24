package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "Standard error response")
public class ErrorResponseDto {

  @Schema(
      description = "Operation success flag",
      example = "false",
      defaultValue = "false"
  )
  @Builder.Default
  private boolean success = false;

  @Schema(
      description = "Main error message",
      example = "Validation failed"
  )
  private String message;

  @Schema(
      description = "Detailed errors message (may be null)",
      example = "[\"Email must be valid\", \"Password is required\"]"
  )
  private List<String> errors;

  @Schema(
      description = "Error timestamp",
      example = "2024-01-15T14:30:00"
  )
  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}
