package com.example.bankcards.service.impl;

import static com.example.bankcards.constants.TestConstants.ANOTHER_USER_ID;
import static com.example.bankcards.constants.TestConstants.ANOTHER_USERNAME;
import static com.example.bankcards.constants.TestConstants.CARD_BELONGS_TO_ANOTHER_USER;
import static com.example.bankcards.constants.TestConstants.CARD_ID;
import static com.example.bankcards.constants.TestConstants.CARD_NOT_FOUND_MESSAGE;
import static com.example.bankcards.constants.TestConstants.DESTINATION_CARD_NOT_FOUND_MESSAGE;
import static com.example.bankcards.constants.TestConstants.DESTINATION_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.DESTINATION_MASKED_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.INSUFFICIENT_BALANCE_MESSAGE;
import static com.example.bankcards.constants.TestConstants.INVALID_CARD_ID;
import static com.example.bankcards.constants.TestConstants.INVALID_TRANSFER_AMOUNT_MESSAGE;
import static com.example.bankcards.constants.TestConstants.INVALID_TRANSFER_AMOUNT;
import static com.example.bankcards.constants.TestConstants.INVALID_USERNAME;
import static com.example.bankcards.constants.TestConstants.PAGE_NUMBER;
import static com.example.bankcards.constants.TestConstants.PAGE_SIZE;
import static com.example.bankcards.constants.TestConstants.SAME_CARD_TRANSFER_MESSAGE;
import static com.example.bankcards.constants.TestConstants.SOURCE_CARD_ID;
import static com.example.bankcards.constants.TestConstants.SOURCE_CARD_NOT_ACTIVE_MESSAGE;
import static com.example.bankcards.constants.TestConstants.SOURCE_CARD_NOT_FOUND_MESSAGE;
import static com.example.bankcards.constants.TestConstants.SOURCE_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.SOURCE_MASKED_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.TARGET_CARD_ID;
import static com.example.bankcards.constants.TestConstants.TRANSFER_AMOUNT;
import static com.example.bankcards.constants.TestConstants.TRANSFER_DESCRIPTION;
import static com.example.bankcards.constants.TestConstants.EXPIRY_DATE;
import static com.example.bankcards.constants.TestConstants.MAX_BALANCE;
import static com.example.bankcards.constants.TestConstants.MIN_BALANCE;
import static com.example.bankcards.constants.TestConstants.TRANSFER_ID_2;
import static com.example.bankcards.constants.TestConstants.USERNAME_USER;
import static com.example.bankcards.constants.TestConstants.USER_ID;
import static com.example.bankcards.constants.TestConstants.TRANSFER_ID;
import static com.example.bankcards.constants.TestConstants.USER_NOT_FOUND_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.mapper.Mapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferServiceImplTest {

  @Mock
  private CardRepository cardRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private TransferRepository transferRepository;

  @Mock
  private Mapper mapper;

  @InjectMocks
  private TransferServiceImpl transferService;

  private User testUser;
  private Card sourceCard;
  private Card destinationCard;
  private Transfer transfer1;
  private Transfer transfer2;
  private TransferRequestDto validRequest;
  private TransferResponseDto dto1;
  private TransferResponseDto dto2;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .build();

    sourceCard = Card.builder()
        .id(SOURCE_CARD_ID)
        .cardNumber(SOURCE_CARD_NUMBER)
        .maskedNumber(SOURCE_MASKED_CARD_NUMBER)
        .balance(MAX_BALANCE)
        .user(testUser)
        .status(CardStatus.ACTIVE)
        .expiryDate(EXPIRY_DATE)
        .build();

    destinationCard = Card.builder()
        .id(TARGET_CARD_ID)
        .cardNumber(DESTINATION_CARD_NUMBER)
        .maskedNumber(DESTINATION_MASKED_CARD_NUMBER)
        .balance(MIN_BALANCE)
        .user(testUser)
        .status(CardStatus.ACTIVE)
        .expiryDate(EXPIRY_DATE)
        .build();

    validRequest = TransferRequestDto.builder()
        .fromCardId(SOURCE_CARD_ID)
        .toCardId(TARGET_CARD_ID)
        .amount(TRANSFER_AMOUNT)
        .description(TRANSFER_DESCRIPTION)
        .build();

    transfer1 = Transfer.builder()
        .id(TRANSFER_ID)
        .fromCard(sourceCard)
        .toCard(destinationCard)
        .amount(TRANSFER_AMOUNT)
        .build();

    transfer2 = Transfer.builder()
        .id(TRANSFER_ID_2)
        .fromCard(destinationCard)
        .toCard(sourceCard)
        .amount(TRANSFER_AMOUNT)
        .build();

    dto1 = TransferResponseDto.builder()
        .id(TRANSFER_ID)
        .fromCardId(sourceCard.getId())
        .fromCardMaskedNumber(sourceCard.getMaskedNumber())
        .toCardId(destinationCard.getId())
        .toCardMaskedNumber(destinationCard.getMaskedNumber())
        .amount(TRANSFER_AMOUNT)
        .build();

    dto2 = TransferResponseDto.builder()
        .id(TRANSFER_ID_2)
        .fromCardId(destinationCard.getId())
        .fromCardMaskedNumber(destinationCard.getMaskedNumber())
        .toCardId(sourceCard.getId())
        .toCardMaskedNumber(sourceCard.getMaskedNumber())
        .amount(TRANSFER_AMOUNT)
        .build();

    when(mapper.toTransferResponseDto(transfer1)).thenReturn(dto1);
    when(mapper.toTransferResponseDto(transfer2)).thenReturn(dto2);
  }

  @Test
  void transferBetweenOwnCards_ShouldSuccessfullyTransferAndReturnResponse() {
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.of(sourceCard));
    when(cardRepository.findByIdAndUserId(TARGET_CARD_ID, USER_ID))
        .thenReturn(Optional.of(destinationCard));
    when(transferRepository.save(any(Transfer.class)))
        .thenAnswer(invocation -> {
          Transfer transfer = invocation.getArgument(0);
          transfer.setId(TRANSFER_ID);
          return transfer;
        });

    TransferResponseDto result = transferService
        .transferBetweenOwnCards(validRequest, USERNAME_USER);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(TRANSFER_ID);
    assertThat(result.getFromCardId()).isEqualTo(SOURCE_CARD_ID);
    assertThat(result.getToCardId()).isEqualTo(TARGET_CARD_ID);
    assertThat(result.getAmount()).isEqualTo(TRANSFER_AMOUNT);
    assertThat(result.getDescription()).isEqualTo(TRANSFER_DESCRIPTION);

    assertThat(sourceCard.getBalance()).isEqualTo(new BigDecimal("4900.00"));
    assertThat(destinationCard.getBalance()).isEqualTo(new BigDecimal("200.00"));

    verify(cardRepository).save(sourceCard);
    verify(cardRepository).save(destinationCard);
    verify(transferRepository).save(any(Transfer.class));
  }

  @Test
  void transferBetweenOwnCards_WhenUserNotFound_ShouldThrowException() {
    when(userRepository.findByUsername(INVALID_USERNAME))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> transferService
        .transferBetweenOwnCards(validRequest, INVALID_USERNAME))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage(USER_NOT_FOUND_MESSAGE);
  }

  @Test
  void transferBetweenOwnCards_WhenSourceCardNotFound_ShouldThrowException() {
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> transferService.transferBetweenOwnCards(validRequest, USERNAME_USER))
        .isInstanceOf(CardNotFoundException.class)
        .hasMessage(SOURCE_CARD_NOT_FOUND_MESSAGE);
  }

  @Test
  void transferBetweenOwnCards_WhenDestinationCardNotFound_ShouldThrowException() {
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.of(sourceCard));
    when(cardRepository.findByIdAndUserId(TARGET_CARD_ID, USER_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> transferService.transferBetweenOwnCards(validRequest, USERNAME_USER))
        .isInstanceOf(CardNotFoundException.class)
        .hasMessage(DESTINATION_CARD_NOT_FOUND_MESSAGE);
  }

  @Test
  void transferBetweenOwnCards_WhenSourceCardInactive_ShouldThrowException() {
    sourceCard.setStatus(CardStatus.BLOCKED);
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.of(sourceCard));
    when(cardRepository.findByIdAndUserId(TARGET_CARD_ID, USER_ID))
        .thenReturn(Optional.of(destinationCard));

    assertThatThrownBy(() -> transferService.transferBetweenOwnCards(validRequest, USERNAME_USER))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage(SOURCE_CARD_NOT_ACTIVE_MESSAGE);
  }

  @Test
  void transferBetweenOwnCards_WhenInsufficientBalance_ShouldThrowException() {
    validRequest.setAmount(new BigDecimal("10000"));
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.of(sourceCard));
    when(cardRepository.findByIdAndUserId(TARGET_CARD_ID, USER_ID))
        .thenReturn(Optional.of(destinationCard));

    assertThatThrownBy(() -> transferService.transferBetweenOwnCards(validRequest, USERNAME_USER))
        .isInstanceOf(InsufficientBalanceException.class)
        .hasMessage(INSUFFICIENT_BALANCE_MESSAGE);
  }

  @Test
  void transferBetweenOwnCards_WhenZeroAmount_ShouldThrowException() {
    validRequest.setAmount(BigDecimal.ZERO);
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.of(sourceCard));
    when(cardRepository.findByIdAndUserId(TARGET_CARD_ID, USER_ID))
        .thenReturn(Optional.of(destinationCard));

    assertThatThrownBy(() -> transferService.transferBetweenOwnCards(validRequest, USERNAME_USER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(INVALID_TRANSFER_AMOUNT_MESSAGE);
  }

  @Test
  void transferBetweenOwnCards_WhenNegativeAmount_ShouldThrowException() {
    validRequest.setAmount(INVALID_TRANSFER_AMOUNT);
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.of(sourceCard));
    when(cardRepository.findByIdAndUserId(TARGET_CARD_ID, USER_ID))
        .thenReturn(Optional.of(destinationCard));

    assertThatThrownBy(() -> transferService.transferBetweenOwnCards(validRequest, USERNAME_USER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(INVALID_TRANSFER_AMOUNT_MESSAGE);
  }

  @Test
  void transferBetweenOwnCards_WhenTransferToSameCard_ShouldThrowException() {
    validRequest.setToCardId(SOURCE_CARD_ID);
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(SOURCE_CARD_ID, USER_ID))
        .thenReturn(Optional.of(sourceCard));

    assertThatThrownBy(() -> transferService.transferBetweenOwnCards(validRequest, USERNAME_USER))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage(SAME_CARD_TRANSFER_MESSAGE);
  }

  @Test
  void getTransferHistory_ShouldReturnUserTransfers() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);

    Page<Transfer> transfersPage = new PageImpl<>(Arrays.asList(transfer1, transfer2));

    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(transferRepository.findByUserId(USER_ID, pageable))
        .thenReturn(transfersPage);

    Page<TransferResponseDto> result = transferService.getTransferHistory(USERNAME_USER, pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getContent().get(0).getId()).isEqualTo(TRANSFER_ID);
    assertThat(result.getContent().get(1).getId()).isEqualTo(TRANSFER_ID_2);

    verify(transferRepository).findByUserId(USER_ID, pageable);
  }

  @Test
  void getTransferHistory_WhenUserNotFound_ShouldThrowException() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    when(userRepository.findByUsername(INVALID_USERNAME))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> transferService.getTransferHistory(INVALID_USERNAME, pageable))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage(USER_NOT_FOUND_MESSAGE);
  }

  @Test
  void getTransferHistory_WhenNoTransfers_ShouldReturnEmptyPage() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    Page<Transfer> emptyPage = Page.empty();

    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(transferRepository.findByUserId(USER_ID, pageable))
        .thenReturn(emptyPage);

    Page<TransferResponseDto> result = transferService.getTransferHistory(USERNAME_USER, pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
  }

  @Test
  void getCardTransferHistory_ShouldReturnCardTransfers() {
    Pageable pageable = PageRequest.of(0, 10);
    Transfer transfer1 = Transfer.builder()
        .id(TRANSFER_ID)
        .fromCard(sourceCard)
        .toCard(destinationCard)
        .amount(TRANSFER_AMOUNT)
        .build();
    Page<Transfer> transfersPage = new PageImpl<>(Collections.singletonList(transfer1));

    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findCardById(SOURCE_CARD_ID))
        .thenReturn(Optional.of(sourceCard));
    when(transferRepository.findByCardId(SOURCE_CARD_ID, pageable))
        .thenReturn(transfersPage);

    Page<TransferResponseDto> result = transferService
        .getCardTransferHistory(SOURCE_CARD_ID, USERNAME_USER, pageable);

    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo(TRANSFER_ID);
    assertThat(result.getContent().get(0).getFromCardId()).isEqualTo(SOURCE_CARD_ID);

    verify(transferRepository).findByCardId(SOURCE_CARD_ID, pageable);
  }

  @Test
  void getCardTransferHistory_WhenUserNotFound_ShouldThrowException() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    when(userRepository.findByUsername(INVALID_USERNAME))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> transferService
        .getCardTransferHistory(CARD_ID, INVALID_USERNAME, pageable))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage(USER_NOT_FOUND_MESSAGE);
  }

  @Test
  void getCardTransferHistory_WhenCardNotFound_ShouldThrowException() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findCardById(INVALID_CARD_ID))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> transferService
        .getCardTransferHistory(INVALID_CARD_ID, USERNAME_USER, pageable))
        .isInstanceOf(CardNotFoundException.class)
        .hasMessage(CARD_NOT_FOUND_MESSAGE);
  }

  @Test
  void getCardTransferHistory_WhenCardBelongsToAnotherUser_ShouldThrowException() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    User anotherUser = User.builder()
        .id(ANOTHER_USER_ID)
        .username(ANOTHER_USERNAME)
        .build();
    sourceCard.setUser(anotherUser);

    when(userRepository.findByUsername(USERNAME_USER))
        .thenReturn(Optional.of(testUser));
    when(cardRepository.findCardById(CARD_ID))
        .thenReturn(Optional.of(sourceCard));

    assertThatThrownBy(() -> transferService
        .getCardTransferHistory(CARD_ID, USERNAME_USER, pageable))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage(CARD_BELONGS_TO_ANOTHER_USER);
  }
}
