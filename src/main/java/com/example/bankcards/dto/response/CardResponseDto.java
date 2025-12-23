package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseDto {

  private String id;
  private String maskedNumber;
  private String cardholder;
  private LocalDate expiryDate;
  private CardStatus status;
  private BigDecimal balance;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
