package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Money transfer between cards request")
public class TransferRequestDto {

  @Schema(
      description = "Source card ID",
      example = "550e8400-e29b-41d4-a716-446655440001",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotBlank(message = "Source card ID is required")
  private String fromCardId;

  @Schema(
      description = "Destination card ID",
      example = "550e8400-e29b-41d4-a716-446655440002",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  @NotBlank(message = "Destination card ID is required")
  private String toCardId;

  @Schema(
      description = "Transfer amount",
      example = "1000.50",
      requiredMode = Schema.RequiredMode.REQUIRED,
      minimum = "0.01",
      maximum = "1000000"
  )
  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @DecimalMax(value = "1000000", message = "Amount must not exceed 1,000,000")
  private BigDecimal amount;

  @Schema(
      description = "Transfer description (not  required)",
      example = "Monthly savings",
      maxLength = 255
  )
  @Size(max = 255, message = "Description must not exceed 255 characters")
  private String description;
}
