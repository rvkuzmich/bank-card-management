package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.InsufficientBalanceException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransferService {

  private final CardRepository cardRepository;
  private final UserRepository userRepository;
  private final TransferRepository transferRepository;

  public TransferResponse transferBetweenOwnCards(TransferRequest request, String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Card fromCard = cardRepository.findByIdAndUserId(request.getFromCardId(), user.getId())
        .orElseThrow(() -> new CardNotFoundException("Source card not found"));

    Card toCard = cardRepository.findByIdAndUserId(request.getToCardId(), user.getId())
        .orElseThrow(() -> new CardNotFoundException("Destination card not found"));

    validateTransfer(fromCard, toCard, request.getAmount());

    fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
    toCard.setBalance(toCard.getBalance().add(request.getAmount()));

    cardRepository.save(fromCard);
    cardRepository.save(toCard);

    Transfer transfer = Transfer.builder()
        .fromCard(fromCard)
        .toCard(toCard)
        .amount(request.getAmount())
        .description(request.getDescription())
        .timestamp(LocalDateTime.now())
        .build();

    transferRepository.save(transfer);

    log.info("Transfer completed from card {} to card {} amount {}",
        fromCard.getId(), toCard.getId(), request.getAmount());

    return TransferResponse.builder()
        .id(transfer.getId())
        .fromCardId(fromCard.getId())
        .toCardId(toCard.getId())
        .amount(request.getAmount())
        .description(request.getDescription())
        .timestamp(transfer.getTimestamp())
        .build();
  }

  @Transactional(readOnly = true)
  public Page<TransferResponse> getTransferHistory(String username, Pageable pageable) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Page<Transfer> transfers = transferRepository.findByUserId(user.getId(), pageable);

    return transfers.map(this::mapToTransferResponse);
  }

  @Transactional(readOnly = true)
  public Page<TransferResponse> getCardTransferHistory(String cardId, String username,
      Pageable pageable) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    Card card = cardRepository.findCardById(cardId)
        .orElseThrow(() -> new CardNotFoundException("Card not found"));

    if (!user.equals(card.getUser())) {
      throw new IllegalStateException("Card does not belong to user");
    }

    Page<Transfer> transfers = transferRepository.findByCardId(cardId, pageable);

    return transfers.map(this::mapToTransferResponse);
  }

  private void validateTransfer(Card fromCard, Card toCard, BigDecimal amount) {
    if (!fromCard.isActive()) {
      throw new IllegalStateException("Source card is not active");
    }

    if (!toCard.isActive()) {
      throw new IllegalStateException("Destination card is not active");
    }

    if (fromCard.getBalance().compareTo(amount) < 0) {
      throw new InsufficientBalanceException("Insufficient balance");
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    if (fromCard.getId().equals(toCard.getId())) {
      throw new IllegalArgumentException("Cannot transfer to the same card");
    }
  }

  private TransferResponse mapToTransferResponse(Transfer transfer) {
    return TransferResponse.builder()
        .id(transfer.getId())
        .fromCardId(transfer.getFromCard().getId())
        .fromCardMaskedNumber(transfer.getFromCard().getMaskedNumber())
        .toCardId(transfer.getToCard().getId())
        .toCardMaskedNumber(transfer.getToCard().getMaskedNumber())
        .amount(transfer.getAmount())
        .description(transfer.getDescription())
        .timestamp(transfer.getTimestamp())
        .build();
  }
}
