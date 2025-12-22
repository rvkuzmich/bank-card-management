package com.example.bankcards.dto.response;

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
public class TransferResponse {

  private String id;
  private String fromCardId;
  private String fromCardMaskedNumber;
  private String toCardId;
  private String toCardMaskedNumber;
  private BigDecimal amount;
  private String description;
  private LocalDateTime timestamp;
}
