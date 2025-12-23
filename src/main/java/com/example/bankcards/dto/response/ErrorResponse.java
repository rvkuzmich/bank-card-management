package com.example.bankcards.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorResponse {

  private boolean success;
  private String message;
  private List<String> errors;

  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();
}