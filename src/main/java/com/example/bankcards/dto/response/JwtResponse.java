package com.example.bankcards.dto.response;

import com.example.bankcards.entity.Role;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

  private String token;
  private String type = "Bearer";
  private String username;
  private String email;
  private Role role;
  private LocalDateTime expiresAt;

  public JwtResponse(String token, String username, String email, Role role,
      LocalDateTime expiresAt) {
    this.token = token;
    this.username = username;
    this.email = email;
    this.role = role;
    this.expiresAt = expiresAt;
  }
}
