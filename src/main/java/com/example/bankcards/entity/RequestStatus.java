package com.example.bankcards.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Requests status (e.g. for card block)",
    enumAsRef = true
)
public enum RequestStatus {

  @Schema(
      description = "Request is approved",
      example = "APPROVED"
  )
  APPROVED,

  @Schema(
      description = "Request is pending",
      example = "PENDING"
  )
  PENDING,

  @Schema(
      description = "Request is rejected",
      example = "REJECTED"
  )
  REJECTED
}
