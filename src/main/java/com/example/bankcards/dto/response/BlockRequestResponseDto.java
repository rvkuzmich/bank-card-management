package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
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
@Schema(description = "Response for card block request")
public class BlockRequestResponseDto {

  @Schema(
      description = "Unique card ID",
      example = "550e8400-e29b-41d4-a716-446655440000"
  )
  private String cardId;

  @Schema(
      description = "Current card status after request",
      example = "PENDING_BLOCK"
  )
  private CardStatus cardStatus;

  @Schema(
      description = "Pending block request presence",
      example = "true"
  )
  private boolean hasPendingRequest;

  @Schema(
      description = "Block request creation timestamp",
      example = "2024-01-15T14:30:00"
  )
  private LocalDateTime requestedAt;

  @Schema(
      description = "Block request creator username",
      example = "john_doe"
  )
  private String requestedBy;

  @Schema(
      description = "Information message",
      example = "Card block request has been sent to administrator for review"
  )
  private String message;
}
