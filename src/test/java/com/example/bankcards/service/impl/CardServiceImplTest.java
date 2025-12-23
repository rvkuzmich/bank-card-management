package com.example.bankcards.service.impl;

import static com.example.bankcards.constants.TestConstants.BALANCE;
import static com.example.bankcards.constants.TestConstants.BLOCK_REQUEST_ID;
import static com.example.bankcards.constants.TestConstants.CARDHOLDER;
import static com.example.bankcards.constants.TestConstants.CARD_ID;
import static com.example.bankcards.constants.TestConstants.CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.EXPIRY_DATE;
import static com.example.bankcards.constants.TestConstants.MASKED_CARD_NUMBER;
import static com.example.bankcards.constants.TestConstants.MIN_BALANCE;
import static com.example.bankcards.constants.TestConstants.PAGE_NUMBER;
import static com.example.bankcards.constants.TestConstants.PAGE_SIZE;
import static com.example.bankcards.constants.TestConstants.USERNAME_USER;
import static com.example.bankcards.constants.TestConstants.USERNAME_ADMIN;
import static com.example.bankcards.constants.TestConstants.USER_EMAIL;
import static com.example.bankcards.constants.TestConstants.USER_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.BlockRequestResponseDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.RequestStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.mapper.Mapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.EncryptionUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

  @Mock
  private CardBlockRequestRepository blockRequestRepository;

  @Mock
  private CardRepository cardRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private EncryptionUtil encryptionUtil;

  @Mock
  private Mapper mapper;

  @Mock
  private CardNumberGenerator cardNumberGenerator;

  @InjectMocks
  private CardServiceImpl cardService;

  private User testUser;
  private Card testCard;
  private CardRequestDto cardRequestDto;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
        .id(USER_ID)
        .username(USERNAME_USER)
        .email(USER_EMAIL)
        .build();

    testCard = Card.builder()
        .id(CARD_ID)
        .cardNumber(CARD_NUMBER)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .expiryDate(EXPIRY_DATE)
        .status(CardStatus.ACTIVE)
        .balance(BALANCE)
        .user(testUser)
        .createdAt(LocalDateTime.now())
        .build();

    cardRequestDto = CardRequestDto.builder()
        .cardholder(CARDHOLDER)
        .cardNumber(CARD_NUMBER)
        .build();
  }

  @Test
  void createCard_ShouldCreateCardSuccessfully() {
    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.of(testUser));
    when(cardNumberGenerator.generateCardNumber()).thenReturn(CARD_NUMBER);
    when(encryptionUtil.encrypt(anyString())).thenReturn(CARD_NUMBER);
    when(encryptionUtil.maskCardNumber(anyString())).thenReturn(MASKED_CARD_NUMBER);

    when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
      Card card = invocation.getArgument(0);
      return Card.builder()
          .id(CARD_ID)
          .cardNumber(card.getCardNumber())
          .maskedNumber(card.getMaskedNumber())
          .cardholder(card.getCardholder())
          .expiryDate(card.getExpiryDate())
          .status(card.getStatus())
          .balance(card.getBalance())
          .user(card.getUser())
          .createdAt(LocalDateTime.now())
          .updatedAt(LocalDateTime.now())
          .build();
    });

    CardResponseDto expectedResponse = CardResponseDto.builder()
        .id(CARD_ID)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .expiryDate(LocalDate.now().plusYears(3))
        .status(CardStatus.ACTIVE)
        .balance(BigDecimal.ZERO)
        .createdAt(LocalDateTime.now())
        .build();

    when(mapper.toCardResponseDto(any(Card.class))).thenReturn(expectedResponse);

    CardResponseDto result = cardService.createCard(cardRequestDto, USERNAME_USER);

    assertNotNull(result);
    assertEquals(CARD_ID, result.getId());
    assertEquals(CARDHOLDER, result.getCardholder());
    assertEquals(MASKED_CARD_NUMBER, result.getMaskedNumber());
    assertEquals(CardStatus.ACTIVE, result.getStatus());
    assertEquals(BigDecimal.ZERO, result.getBalance());

    verify(userRepository).findByUsername(USERNAME_USER);
    verify(cardNumberGenerator).generateCardNumber();
    verify(encryptionUtil).encrypt(CARD_NUMBER);
    verify(encryptionUtil).maskCardNumber(CARD_NUMBER);
    verify(cardRepository).save(any(Card.class));
    verify(mapper).toCardResponseDto(any(Card.class));
  }

  @Test
  void createCard_WhenUserNotFound_ShouldThrowException() {
    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> cardService.createCard(cardRequestDto, USERNAME_USER));

    verify(userRepository).findByUsername(USERNAME_USER);
    verify(cardRepository, never()).save(any(Card.class));
  }

  @Test
  void getUserCards_ShouldReturnFilteredCards() {
    CardFilterRequestDto filter = CardFilterRequestDto.builder()
        .status(CardStatus.ACTIVE)
        .minBalance(MIN_BALANCE)
        .build();

    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    Page<Card> cardPage = new PageImpl<>(Collections.singletonList(testCard), pageable, 1);

    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.of(testUser));
    when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);

    Page<CardResponseDto> result = cardService.getUserCards(USERNAME_USER, filter, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    assertEquals(CARDHOLDER, result.getContent().get(0).getCardholder());

    verify(userRepository).findByUsername(USERNAME_USER);
    verify(cardRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void getUserCards_WithNullFilter_ShouldReturnAllCards() {
    Pageable pageable = PageRequest.of(PAGE_NUMBER, PAGE_SIZE);
    Page<Card> cardPage = new PageImpl<>(Collections.singletonList(testCard), pageable, 1);

    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.of(testUser));
    when(cardRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(cardPage);

    Page<CardResponseDto> result = cardService.getUserCards(USERNAME_USER, null, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());

    verify(cardRepository).findAll(any(Specification.class), eq(pageable));
  }

  @Test
  void blockCard_AsAdmin_ShouldBlockCardSuccessfully() {
    CardResponseDto expectedResponse = CardResponseDto.builder()
        .id(CARD_ID)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .status(CardStatus.ACTIVE)
        .balance(BALANCE)
        .build();

    when(cardRepository.findCardById(CARD_ID)).thenReturn(Optional.of(testCard));
    when(cardRepository.save(any(Card.class))).thenReturn(testCard);
    when(mapper.toCardResponseDto(testCard)).thenReturn(expectedResponse);

    CardResponseDto result = cardService.blockCard(CARD_ID, USERNAME_ADMIN);

    assertNotNull(result);
    assertEquals(CardStatus.BLOCKED, testCard.getStatus());
    assertEquals(USERNAME_ADMIN, testCard.getBlockedBy());
    assertNotNull(testCard.getBlockedAt());

    verify(cardRepository).findCardById(CARD_ID);
    verify(cardRepository).save(testCard);
  }

  @Test
  void blockCard_WhenCardNotFound_ShouldThrowException() {
    when(cardRepository.findCardById(CARD_ID)).thenReturn(Optional.empty());

    assertThrows(CardNotFoundException.class,
        () -> cardService.blockCard(CARD_ID, USERNAME_ADMIN));

    verify(cardRepository).findCardById(CARD_ID);
    verify(cardRepository, never()).save(any(Card.class));
  }

  @Test
  void blockCard_WhenCardExpired_ShouldThrowException() {
    testCard.setExpiryDate(LocalDate.now().minusDays(1));
    when(cardRepository.findCardById(CARD_ID)).thenReturn(Optional.of(testCard));

    assertThrows(IllegalStateException.class,
        () -> cardService.blockCard(CARD_ID, USERNAME_ADMIN));

    verify(cardRepository).findCardById(CARD_ID);
    verify(cardRepository, never()).save(any(Card.class));
  }

  @Test
  void requestCardBlock_ShouldCreateBlockRequest() {
    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(CARD_ID, testUser.getId())).thenReturn(
        Optional.of(testCard));
    when(blockRequestRepository.save(any(CardBlockRequest.class))).thenReturn(
        new CardBlockRequest());

    BlockRequestResponseDto result = cardService.requestCardBlock(CARD_ID, USERNAME_USER);

    assertNotNull(result);
    assertEquals(CARD_ID, result.getCardId());
    assertEquals(CardStatus.PENDING_BLOCK, testCard.getStatus());
    assertTrue(result.isHasPendingRequest());
    assertEquals(USERNAME_USER, result.getRequestedBy());

    verify(userRepository).findByUsername(USERNAME_USER);
    verify(cardRepository).findByIdAndUserId(CARD_ID, testUser.getId());
    verify(blockRequestRepository).save(any(CardBlockRequest.class));
    verify(cardRepository).save(testCard);
  }

  @Test
  void requestCardBlock_WhenCardAlreadyBlocked_ShouldThrowException() {
    testCard.setStatus(CardStatus.BLOCKED);
    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(CARD_ID, testUser.getId())).thenReturn(
        Optional.of(testCard));

    assertThrows(IllegalStateException.class,
        () -> cardService.requestCardBlock(CARD_ID, USERNAME_USER));

    verify(blockRequestRepository, never()).save(any(CardBlockRequest.class));
  }

  @Test
  void approveBlockCard_ShouldApproveBlockRequest() {
    testCard.setStatus(CardStatus.PENDING_BLOCK);
    CardBlockRequest blockRequest = CardBlockRequest.builder()
        .id(BLOCK_REQUEST_ID)
        .card(testCard)
        .requestedBy(USERNAME_USER)
        .requestedAt(LocalDateTime.now())
        .status(RequestStatus.PENDING)
        .build();
    CardResponseDto expectedResponse = CardResponseDto.builder()
        .id(CARD_ID)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .status(CardStatus.ACTIVE)
        .balance(BALANCE)
        .build();

    when(cardRepository.findCardById(CARD_ID)).thenReturn(Optional.of(testCard));
    when(blockRequestRepository.findByCardIdAndStatus(CARD_ID, RequestStatus.PENDING))
        .thenReturn(Optional.of(blockRequest));
    when(cardRepository.save(any(Card.class))).thenReturn(testCard);
    when(blockRequestRepository.save(any(CardBlockRequest.class))).thenReturn(blockRequest);
    when(mapper.toCardResponseDto(testCard)).thenReturn(expectedResponse);

    CardResponseDto result = cardService.approveBlockCard(CARD_ID, USERNAME_ADMIN);

    assertNotNull(result);
    assertEquals(CardStatus.BLOCKED, testCard.getStatus());
    assertEquals(USERNAME_ADMIN, testCard.getBlockedBy());
    assertEquals(RequestStatus.APPROVED, blockRequest.getStatus());
    assertEquals(USERNAME_ADMIN, blockRequest.getApprovedBy());

    verify(cardRepository).findCardById(CARD_ID);
    verify(blockRequestRepository).findByCardIdAndStatus(CARD_ID, RequestStatus.PENDING);
    verify(cardRepository).save(testCard);
    verify(blockRequestRepository).save(blockRequest);
  }

  @Test
  void approveBlockCard_WhenNoPendingRequest_ShouldThrowException() {
    testCard.setStatus(CardStatus.ACTIVE);
    when(cardRepository.findCardById(CARD_ID)).thenReturn(Optional.of(testCard));

    assertThrows(IllegalStateException.class,
        () -> cardService.approveBlockCard(CARD_ID, USERNAME_ADMIN));

    verify(blockRequestRepository, never()).save(any(CardBlockRequest.class));
  }

  @Test
  void activateCard_ShouldActivateCardSuccessfully() {
    CardResponseDto expectedResponse = CardResponseDto.builder()
        .id(CARD_ID)
        .maskedNumber(MASKED_CARD_NUMBER)
        .cardholder(CARDHOLDER)
        .status(CardStatus.ACTIVE)
        .balance(BALANCE)
        .build();

    testCard.setStatus(CardStatus.BLOCKED);
    when(cardRepository.findCardById(CARD_ID)).thenReturn(Optional.of(testCard));
    when(cardRepository.save(any(Card.class))).thenReturn(testCard);
    when(mapper.toCardResponseDto(testCard)).thenReturn(expectedResponse);

    CardResponseDto result = cardService.activateCard(CARD_ID, USERNAME_ADMIN);

    assertNotNull(result);
    assertEquals(CardStatus.ACTIVE, testCard.getStatus());

    verify(cardRepository).findCardById(CARD_ID);
    verify(cardRepository).save(testCard);
  }

  @Test
  void activateCard_WhenCardExpired_ShouldThrowException() {
    testCard.setExpiryDate(LocalDate.now().minusDays(1));
    when(cardRepository.findCardById(CARD_ID)).thenReturn(Optional.of(testCard));

    assertThrows(IllegalStateException.class,
        () -> cardService.activateCard(CARD_ID, USERNAME_ADMIN));

    verify(cardRepository, never()).save(any(Card.class));
  }

  @Test
  void getCardBalance_ShouldReturnBalance() {
    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(CARD_ID, testUser.getId())).thenReturn(
        Optional.of(testCard));

    BigDecimal result = cardService.getCardBalance(CARD_ID, USERNAME_USER);

    assertNotNull(result);
    assertEquals(BALANCE, result);

    verify(userRepository).findByUsername(USERNAME_USER);
    verify(cardRepository).findByIdAndUserId(CARD_ID, testUser.getId());
  }

  @Test
  void getCardBalance_WhenCardNotFound_ShouldThrowException() {
    when(userRepository.findByUsername(USERNAME_USER)).thenReturn(Optional.of(testUser));
    when(cardRepository.findByIdAndUserId(CARD_ID, testUser.getId())).thenReturn(
        Optional.empty());

    assertThrows(CardNotFoundException.class,
        () -> cardService.getCardBalance(CARD_ID, USERNAME_USER));

    verify(userRepository).findByUsername(USERNAME_USER);
    verify(cardRepository).findByIdAndUserId(CARD_ID, testUser.getId());
  }
}
