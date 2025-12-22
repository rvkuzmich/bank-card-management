package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.CardFilterRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.EncryptionUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final EncryptionUtil encryptionUtil;

  @Override
  @Transactional
  public CardResponse createCard(CardRequest request, String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String cardNumber = generateCardNumber();
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
    return mapToResponse(card);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<CardResponse> getUserCards(String username, CardFilterRequest filter,
      Pageable pageable) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Specification<Card> spec = Specification.where((root, query, cb) ->
        cb.equal(root.get("user").get("id"), user.getId()));

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

    Page<Card> cards = cardRepository.findAll(spec, pageable);
    return cards.map(this::mapToResponse);
  }

  @Override
  @Transactional
  public CardResponse blockCard(String cardId, String username) {
    Card card = getCardByIdAndUser(cardId, username);

    if (card.isExpired()) {
      throw new IllegalStateException("Cannot block expired card");
    }

    card.setStatus(CardStatus.BLOCKED);
    card = cardRepository.save(card);

    return mapToResponse(card);
  }

  @Override
  @Transactional
  public CardResponse activateCard(String cardId, String username) {
    Card card = getCardByIdAndUser(cardId, username);

    if (card.isExpired()) {
      throw new IllegalStateException("Cannot activate expired card");
    }

    card.setStatus(CardStatus.ACTIVE);
    card = cardRepository.save(card);

    return mapToResponse(card);
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

  private String generateCardNumber() {
    Random random = new Random();
    StringBuilder cardNumber = new StringBuilder();
    for (int i = 0; i < 4; i++) {
      cardNumber.append(String.format("%04d", random.nextInt(10000)));
      if (i < 3) {
        cardNumber.append(" ");
      }
    }
    return cardNumber.toString();
  }

  private CardResponse mapToResponse(Card card) {
    return CardResponse.builder()
        .id(card.getId())
        .maskedNumber(card.getMaskedNumber())
        .cardholder(card.getCardholder())
        .expiryDate(card.getExpiryDate())
        .status(card.getStatus())
        .balance(card.getBalance())
        .createdAt(card.getCreatedAt())
        .build();
  }
}
