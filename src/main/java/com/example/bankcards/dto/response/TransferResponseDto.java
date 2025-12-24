package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Completed transfer information")
public class TransferResponseDto {

  @Schema(
      description = "Unique transfer ID",
      example = "556e8411-e25b-31c2-b251-423433660324"
  )
  private String id;

  @Schema(
      description = "Source card ID",
      example = "550e8400-e29b-41d4-a716-446655440001"
  )
  private String fromCardId;

  @Schema(
      description = "Source card masked number",
      example = "**** **** **** 1234"
  )
  private String fromCardMaskedNumber;

  @Schema(
      description = "Destination card ID",
      example = "550e8400-e29b-41d4-a716-446655440002"
  )
  private String toCardId;

  @Schema(
      description = "Destination card masked number",
      example = "**** **** **** 5678"
  )
  private String toCardMaskedNumber;

  @Schema(
      description = "Transfer amount",
      example = "1000.50"
  )
  private BigDecimal amount;

  @Schema(
      description = "Transfer description",
      example = "Monthly savings"
  )
  private String description;

  @Schema(
      description = "Transfer timestamp",
      example = "2024-01-15T14:30:00"
  )
  private LocalDateTime timestamp;
}
