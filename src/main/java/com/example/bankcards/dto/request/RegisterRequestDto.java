package com.example.bankcards.dto.request;

import com.example.bankcards.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(description = "New user registration request")
public class RegisterRequestDto {

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
      description = "User email",
      example = "r.kuzmich@example.com",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

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

  @Schema(
      description = "User first name",
      example = "Roman",
      maxLength = 50
  )
  @Size(max = 50, message = "First name must not exceed 50 characters")
  private String firstName;

  @Schema(
      description = "User last name",
      example = "Kuzmich",
      maxLength = 50
  )
  @Size(max = 50, message = "Last name must not exceed 50 characters")
  private String lastName;

  @Schema(
      description = "User role",
      example = "USER",
      defaultValue = "USER",
      allowableValues = {"USER", "ADMIN"}
  )
  private Role role;
}
