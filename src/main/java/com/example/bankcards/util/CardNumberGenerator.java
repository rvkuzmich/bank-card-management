package com.example.bankcards.util;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardNumberGenerator {

  private final Random random = new Random();

  public String generateCardNumber() {
    StringBuilder cardNumber = new StringBuilder();
    for (int i = 0; i < 4; i++) {
      cardNumber.append(String.format("%04d", random.nextInt(10000)));
      if (i < 3) {
        cardNumber.append(" ");
      }
    }
    return cardNumber.toString();
  }
}
