package com.example.bankcards.dto.request;

import com.example.bankcards.entity.CardStatus;
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
public class CardFilterRequestDto {

  private CardStatus status;

  @DecimalMin(value = "0", message = "Minimum balance cannot be negative")
  private BigDecimal minBalance;

  @DecimalMin(value = "0", message = "Maximum balance cannot be negative")
  private BigDecimal maxBalance;

  private LocalDate expiryFrom;

  private LocalDate expiryTo;

  private String cardholderContains;

  @AssertTrue(message = "Min balance must be less than or equal to max balance")
  public boolean isBalanceRangeValid() {
    if (minBalance == null || maxBalance == null) {
      return true;
    }
    return minBalance.compareTo(maxBalance) <= 0;
  }
}
