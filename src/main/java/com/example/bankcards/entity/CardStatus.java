package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Статусы банковской карты")
public enum CardStatus {

  @Schema(
      description = "Card is active and can be used for operations",
      example = "ACTIVE"
  )
  ACTIVE,

  @Schema(
      description = "Card is blocked, operations not allowed",
      example = "BLOCKED"
  )
  BLOCKED,

  @Schema(
      description = "Card is expired",
      example = "EXPIRED"
  )
  EXPIRED,

  @Schema(
      description = "Pending for block (admin approval requires)",
      example = "PENDING_BLOCK"
  )
  PENDING_BLOCK
}
