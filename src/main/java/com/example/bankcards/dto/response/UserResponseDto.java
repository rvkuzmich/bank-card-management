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
public class UserResponseDto {

  private String id;
  private String username;
  private String email;
  private String firstName;
  private String lastName;
  private Role role;
  private boolean enabled;
  private LocalDateTime createdAt;
  private int cardCount;
}
