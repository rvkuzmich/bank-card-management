package com.example.bankcards.mapper;

import static com.example.bankcards.constants.TestConstants.BALANCE;
import static com.example.bankcards.constants.TestConstants.CARDHOLDER;
import static com.example.bankcards.constants.TestConstants.CARD_ID;
import static com.example.bankcards.constants.TestConstants.CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.ENCODED_PASSWORD;
import static com.example.bankcards.constants.TestConstants.EXPIRY_DATE;
import static com.example.bankcards.constants.TestConstants.MASKED_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.TRANSFER_AMOUNT;
import static com.example.bankcards.constants.TestConstants.TRANSFER_DESCRIPTION;
import static com.example.bankcards.constants.TestConstants.TRANSFER_ID;
import static com.example.bankcards.constants.TestConstants.USERNAME_USER;
import static com.example.bankcards.constants.TestConstants.USER_EMAIL;
import static com.example.bankcards.constants.TestConstants.USER_FIRSTNAME;
import static com.example.bankcards.constants.TestConstants.USER_ID;
import static com.example.bankcards.constants.TestConstants.USER_LASTNAME;
import static org.junit.jupiter.api.Assertions.*;

import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MapperTest {

  @InjectMocks
  private Mapper mapper;

  private Card testCard;
  private Transfer testTransfer;
  private User user;

  @BeforeEach
  void setUp() {
    testCard = Card.builder()
        .id(CARD_ID)
        .cardNumber(CARD_NUMBER)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .expiryDate(EXPIRY_DATE)
        .status(CardStatus.ACTIVE)
        .balance(BALANCE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    testTransfer = Transfer.builder()
        .id(TRANSFER_ID)
        .fromCard(testCard)
        .toCard(testCard)
        .amount(TRANSFER_AMOUNT)
        .description(TRANSFER_DESCRIPTION)
        .timestamp(LocalDateTime.now())
        .build();

    user = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .password(ENCODED_PASSWORD)
        .firstName(USER_FIRSTNAME)
        .lastName(USER_LASTNAME)
        .role(Role.USER)
        .enabled(true)
        .cards(Collections.emptyList())
        .createdAt(LocalDateTime.now())
        .build();
  }

  @Test
  void toCardResponseDto_ShouldMapAllFields() {
    CardResponseDto result = mapper.toCardResponseDto(testCard);

    assertNotNull(result);
    assertEquals(testCard.getId(), result.getId());
    assertEquals(testCard.getMaskedNumber(), result.getMaskedNumber());
    assertEquals(testCard.getCardholder(), result.getCardholder());
    assertEquals(testCard.getExpiryDate(), result.getExpiryDate());
    assertEquals(testCard.getStatus(), result.getStatus());
    assertEquals(testCard.getBalance(), result.getBalance());
    assertEquals(testCard.getCreatedAt(), result.getCreatedAt());
    assertEquals(testCard.getUpdatedAt(), result.getUpdatedAt());
  }

  @Test
  void toTransferDto_ShouldMapAllFields() {
    TransferResponseDto result = mapper.toTransferResponseDto(testTransfer);

    assertNotNull(result);
    assertEquals(testTransfer.getId(), result.getId());
    assertEquals(testTransfer.getFromCard().getId(), result.getFromCardId());
    assertEquals(testTransfer.getToCard().getId(), result.getToCardId());
    assertEquals(testTransfer.getAmount(), result.getAmount());
    assertEquals(testTransfer.getDescription(), result.getDescription());
    assertEquals(testTransfer.getTimestamp(), result.getTimestamp());
  }

  @Test
  void toUserResponseDto_ShouldMapAllFields() {
    UserResponseDto result = mapper.toUserResponseDto(user);

    assertNotNull(result);
    assertEquals(user.getId(), result.getId());
    assertEquals(user.getUsername(), result.getUsername());
    assertEquals(user.getEmail(), result.getEmail());
    assertEquals(user.getCards().size(), result.getCardCount());
    assertEquals(user.getFirstName(), result.getFirstName());
    assertEquals(user.getLastName(), result.getLastName());
  }
}
