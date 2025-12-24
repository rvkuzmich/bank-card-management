package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request for new card creation")
public class CardRequestDto {

  @Schema(
      description = "Cardholder name",
      example = "Roman Kuzmich",
      requiredMode = Schema.RequiredMode.REQUIRED,
      minLength = 2,
      maxLength = 100
  )
  @NotBlank(message = "Cardholder name is required")
  @Size(min = 2, max = 100, message = "Cardholder name must be between 2 and 100 characters")
  private String cardholder;

  @Schema(
      description = "Card number (16 digits)",
      example = "4111111111111111",
      requiredMode = Schema.RequiredMode.REQUIRED,
      pattern = "^[0-9]{16}$"
  )
  @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
  private String cardNumber;

  @Schema(
      description = "Card expiry date",
      example = "2026-12-31",
      requiredMode = Schema.RequiredMode.REQUIRED
  )
  private LocalDate expiryDate;
}
