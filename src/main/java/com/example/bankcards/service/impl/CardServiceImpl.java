package com.example.bankcards.service.impl;

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
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.EncryptionUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

  private final CardBlockRequestRepository blockRequestRepository;
  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final EncryptionUtil encryptionUtil;
  private final Mapper mapper;
  private final CardNumberGenerator cardNumberGenerator;

  @Override
  @Transactional
  public CardResponseDto createCard(CardRequestDto request, String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String cardNumber = cardNumberGenerator.generateCardNumber();
    String encryptedCardNumber = encryptionUtil.encrypt(cardNumber);
    String maskedNumber = encryptionUtil.maskCardNumber(cardNumber);

    Card card = Card.builder()
        .cardNumber(encryptedCardNumber)
        .maskedNumber(maskedNumber)
        .cardholder(request.getCardholder())
        .expiryDate(LocalDate.now().plusYears(3))
        .status(CardStatus.ACTIVE)
        .balance(BigDecimal.ZERO)
        .user(user)
        .build();

    card = cardRepository.save(card);
    return mapper.toCardResponseDto(card);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponseDto> getUserCards(String username, CardFilterRequestDto filter,
      Pageable pageable) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Specification<Card> spec = Specification.where((root, query, cb) ->
        cb.equal(root.get("user").get("id"), user.getId()));

    if (filter != null) {
      if (filter.getStatus() != null) {
        spec = spec.and((root, query, cb) ->
            cb.equal(root.get("status"), filter.getStatus()));
      }

      if (filter.getMinBalance() != null) {
        spec = spec.and((root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("balance"), filter.getMinBalance()));
      }

      if (filter.getMaxBalance() != null) {
        spec = spec.and((root, query, cb) ->
            cb.lessThanOrEqualTo(root.get("balance"), filter.getMaxBalance()));
      }

      if (filter.getExpiryFrom() != null) {
        spec = spec.and((root, query, cb) ->
            cb.greaterThanOrEqualTo(root.get("expiryDate"), filter.getExpiryFrom()));
      }

      if (filter.getExpiryTo() != null) {
        spec = spec.and((root, query, cb) ->
            cb.lessThanOrEqualTo(root.get("expiryDate"), filter.getExpiryTo()));
      }

      if (StringUtils.hasText(filter.getCardholderContains())) {
        spec = spec.and((root, query, cb) ->
            cb.like(cb.lower(root.get("cardholder")),
                "%" + filter.getCardholderContains().toLowerCase() + "%"));
      }
    }

    Page<Card> cards = cardRepository.findAll(spec, pageable);

    return cards.map(mapper::toCardResponseDto);
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public CardResponseDto blockCard(String cardId, String adminUsername) {
    Card card = cardRepository.findCardById(cardId)
        .orElseThrow(() -> new CardNotFoundException("Card not found"));

    if (card.isExpired()) {
      throw new IllegalStateException("Cannot block expired card");
    }

    card.setStatus(CardStatus.BLOCKED);
    card.setBlockedBy(adminUsername);
    card.setBlockedAt(LocalDateTime.now());
    card = cardRepository.save(card);

    log.info("Card {} blocked by admin {}", cardId, adminUsername);

    return mapper.toCardResponseDto(card);
  }

  @Override
  @Transactional
  public BlockRequestResponseDto requestCardBlock(String cardId, String username) {
    Card card = getCardByIdAndUser(cardId, username);

    if (card.isExpired()) {
      throw new IllegalStateException("Cannot block expired card");
    }

    if (card.getStatus() == CardStatus.BLOCKED) {
      throw new IllegalStateException("Card is already blocked");
    }

    if (card.getStatus() == CardStatus.PENDING_BLOCK) {
      throw new IllegalStateException("Block request already pending");
    }

    CardBlockRequest blockRequest = CardBlockRequest.builder()
        .card(card)
        .requestedBy(username)
        .requestedAt(LocalDateTime.now())
        .status(RequestStatus.PENDING)
        .build();

    blockRequestRepository.save(blockRequest);

    card.setStatus(CardStatus.PENDING_BLOCK);
    cardRepository.save(card);

    log.info("Block request for card {} created by user {}", cardId, username);

    return BlockRequestResponseDto.builder()
        .cardId(cardId)
        .cardStatus(card.getStatus())
        .hasPendingRequest(true)
        .requestedAt(LocalDateTime.now())
        .requestedBy(username)
        .message("Запрос на блокировку карты отправлен администратору")
        .build();
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public CardResponseDto approveBlockCard(String cardId, String adminUsername) {
    Card card = cardRepository.findCardById(cardId)
        .orElseThrow(() -> new CardNotFoundException("Card not found"));

    if (card.getStatus() != CardStatus.PENDING_BLOCK) {
      throw new IllegalStateException("No pending block request for this card");
    }

    CardBlockRequest blockRequest = blockRequestRepository
        .findByCardIdAndStatus(cardId, RequestStatus.PENDING)
        .orElseThrow(() -> new IllegalStateException("Block request not found"));

    blockRequest.setApprovedBy(adminUsername);
    blockRequest.setApprovedAt(LocalDateTime.now());
    blockRequest.setStatus(RequestStatus.APPROVED);
    blockRequestRepository.save(blockRequest);

    card.setStatus(CardStatus.BLOCKED);
    card.setBlockedBy(adminUsername);
    card.setBlockedAt(LocalDateTime.now());
    card = cardRepository.save(card);

    log.info("Card {} block approved by admin {}", cardId, adminUsername);

    return mapper.toCardResponseDto(card);
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public CardResponseDto activateCard(String cardId, String adminUsername) {
    Card card = cardRepository.findCardById(cardId)
        .orElseThrow(() -> new CardNotFoundException("Card not found"));

    if (card.isExpired()) {
      throw new IllegalStateException("Cannot activate expired card");
    }

    card.setStatus(CardStatus.ACTIVE);
    card = cardRepository.save(card);

    log.info("Card {} activated by admin {}", cardId, adminUsername);

    return mapper.toCardResponseDto(card);
  }

  @Override
  @Transactional(readOnly = true)
  public BigDecimal getCardBalance(String cardId, String username) {
    Card card = getCardByIdAndUser(cardId, username);
    return card.getBalance();
  }

  private Card getCardByIdAndUser(String cardId, String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return cardRepository.findByIdAndUserId(cardId, user.getId())
        .orElseThrow(() -> new CardNotFoundException("Card not found"));
  }
}
