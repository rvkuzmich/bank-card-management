package com.example.bankcards.dto.response;

import com.example.bankcards.entity.Role;
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
@Schema(description = "JWT token response after successful authentication")
public class JwtResponseDto {

  @Schema(
      description = "JWT token for authentication",
      example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  )
  private String token;

  @Schema(
      description = "Token type",
      example = "Bearer",
      defaultValue = "Bearer"
  )
  private String type = "Bearer";

  @Schema(
      description = "User login",
      example = "rvkuzmich"
  )
  private String username;

  @Schema(
      description = "User email",
      example = "r.kuzmich@example.com"
  )
  private String email;

  @Schema(
      description = "User role",
      example = "USER"
  )
  private Role role;

  @Schema(
      description = "Token expiration timestamp",
      example = "2024-12-31T23:59:59"
  )
  private LocalDateTime expiresAt;

  public JwtResponseDto(String token, String username, String email, Role role,
      LocalDateTime expiresAt) {
    this.token = token;
    this.username = username;
    this.email = email;
    this.role = role;
    this.expiresAt = expiresAt;
  }
}
