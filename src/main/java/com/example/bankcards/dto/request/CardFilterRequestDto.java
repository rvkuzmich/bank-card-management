package com.example.bankcards.dto.request;

import com.example.bankcards.entity.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Card search filter")
public class CardFilterRequestDto {

  @Schema(
      description = "Card status",
      example = "ACTIVE",
      allowableValues = {"ACTIVE", "BLOCKED", "EXPIRED", "PENDING_BLOCK"}
  )
  private CardStatus status;

  @Schema(
      description = "Minimum balance",
      example = "100.00"
  )
  @DecimalMin(value = "0", message = "Minimum balance cannot be negative")
  private BigDecimal minBalance;

  @Schema(
      description = "Maximum balance",
      example = "5000.00"
  )
  @DecimalMin(value = "0", message = "Maximum balance cannot be negative")
  private BigDecimal maxBalance;

  @Schema(
      description = "Expiry date from",
      example = "2024-06-01"
  )
  private LocalDate expiryFrom;

  @Schema(
      description = "Expiry date to",
      example = "2025-12-31"
  )
  private LocalDate expiryTo;

  @Schema(
      description = "Part of the cardholder's name to search for",
      example = "Roman"
  )
  private String cardholderContains;

  @Schema(
      description = "Balance range validation",
      hidden = true
  )
  @AssertTrue(message = "Min balance must be less than or equal to max balance")
  public boolean isBalanceRangeValid() {
    if (minBalance == null || maxBalance == null) {
      return true;
    }
    return minBalance.compareTo(maxBalance) <= 0;
  }
}
