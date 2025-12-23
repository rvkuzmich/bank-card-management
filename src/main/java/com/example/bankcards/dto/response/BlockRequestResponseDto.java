package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockRequestResponseDto {

  private String cardId;
  private CardStatus cardStatus;
  private boolean hasPendingRequest;
  private LocalDateTime requestedAt;
  private String requestedBy;
  private String message;
}