package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "System user roles")
public enum Role {

  @Schema(description = "Regular user")
  USER,

  @Schema(description = "System admin")
  ADMIN
}
