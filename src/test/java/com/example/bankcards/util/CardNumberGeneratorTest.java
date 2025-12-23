package com.example.bankcards.util;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CardNumberGeneratorTest {

  @InjectMocks
  private CardNumberGenerator cardNumberGenerator;

  @Test
  void generateCardNumber_ShouldGenerateValidFormat() {
    String cardNumber = cardNumberGenerator.generateCardNumber();

    assertNotNull(cardNumber);
    assertTrue(cardNumber.matches("\\d{4} \\d{4} \\d{4} \\d{4}"));
  }

  @Test
  void generateCardNumber_ShouldGenerateDifferentNumbers() {
    String cardNumber1 = cardNumberGenerator.generateCardNumber();
    String cardNumber2 = cardNumberGenerator.generateCardNumber();

    assertNotEquals(cardNumber1, cardNumber2);
  }
}