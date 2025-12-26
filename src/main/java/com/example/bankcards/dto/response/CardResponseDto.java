package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Bank card information")
public class CardResponseDto {

  @Schema(
      description = "Unique card ID",
      example = "550e8400-e29b-41d4-a716-446655440000"
  )
  private UUID id;

  @Schema(
      description = "Masked card number",
      example = "**** **** **** 1234"
  )
  private String maskedNumber;

  @Schema(
      description = "Cardholder name",
      example = "Roman Kuzmich"
  )
  private String cardholder;

  @Schema(
      description = "Expiry date",
      example = "2026-12-31"
  )
  private LocalDate expiryDate;

  @Schema(
      description = "Card status",
      example = "ACTIVE"
  )
  private CardStatus status;

  @Schema(
      description = "Current card balance",
      example = "1500.75"
  )
  private BigDecimal balance;

  @Schema(
      description = "Card creation timestamp",
      example = "2024-01-15T10:30:00"
  )
  private LocalDateTime createdAt;

  @Schema(
      description = "Last update timestamp",
      example = "2024-01-15T14:45:00"
  )
  private LocalDateTime updatedAt;
}
