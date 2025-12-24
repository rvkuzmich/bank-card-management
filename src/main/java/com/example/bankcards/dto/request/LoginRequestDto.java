package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "User authentication request")
public class LoginRequestDto {

  @Schema(
      description = "User login",
      example = "rvkuzmich",
      requiredMode = Schema.RequiredMode.REQUIRED,
      minLength = 3,
      maxLength = 50
  )
  @NotBlank(message = "Username is required")
  @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
  private String username;

  @Schema(
      description = "User password",
      example = "MySecureP@ssw0rd123",
      requiredMode = Schema.RequiredMode.REQUIRED,
      minLength = 6,
      maxLength = 100
  )
  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
  private String password;
}
