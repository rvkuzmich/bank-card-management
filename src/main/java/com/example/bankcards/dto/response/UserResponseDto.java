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
@Schema(description = "User information")
public class UserResponseDto {

  @Schema(
      description = "Unique user ID",
      example = "550e8400-e29b-41d4-a716-446655440000"
  )
  private String id;

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
      description = "User first name",
      example = "Roman"
  )
  private String firstName;

  @Schema(
      description = "User last name",
      example = "Kuzmich"
  )
  private String lastName;

  @Schema(
      description = "User role",
      example = "USER"
  )
  private Role role;

  @Schema(
      description = "Is user account enabled",
      example = "true"
  )
  private boolean enabled;

  @Schema(
      description = "User account creation timestamp",
      example = "2024-01-15T10:30:00"
  )
  private LocalDateTime createdAt;

  @Schema(
      description = "User cards count",
      example = "3"
  )
  private int cardCount;
}
