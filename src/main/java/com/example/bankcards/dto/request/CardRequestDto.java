package com.example.bankcards.dto.request;

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
public class CardRequestDto {

  @NotBlank(message = "Cardholder name is required")
  @Size(min = 2, max = 100, message = "Cardholder name must be between 2 and 100 characters")
  private String cardholder;

  @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
  private String cardNumber;

  private LocalDate expiryDate;
}
