package com.example.bankcards.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDto {

  @NotBlank(message = "Source card ID is required")
  private String fromCardId;

  @NotBlank(message = "Destination card ID is required")
  private String toCardId;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @DecimalMax(value = "1000000", message = "Amount must not exceed 1,000,000")
  private BigDecimal amount;

  @Size(max = 255, message = "Description must not exceed 255 characters")
  private String description;
}
