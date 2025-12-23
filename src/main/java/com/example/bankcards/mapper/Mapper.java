package com.example.bankcards.mapper;

import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.dto.response.UserResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

  public static CardResponseDto toCardResponseDtoStatic(Card card) {
    return CardResponseDto.builder()
        .id(card.getId())
        .maskedNumber(card.getMaskedNumber())
        .cardholder(card.getCardholder())
        .expiryDate(card.getExpiryDate())
        .status(card.getStatus())
        .balance(card.getBalance())
        .createdAt(card.getCreatedAt())
        .updatedAt(card.getUpdatedAt())
        .build();
  }

  public CardResponseDto toCardResponseDto(Card card) {
    return toCardResponseDtoStatic(card);
  }

  public static TransferResponseDto toTransferResponseStatic(Transfer transfer) {
    return TransferResponseDto.builder()
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

  public TransferResponseDto toTransferResponseDto(Transfer transfer) {
    return toTransferResponseStatic(transfer);
  }

  public static UserResponseDto toUserResponseStatic(User user) {
    int cardCount = user.getCards() != null ? user.getCards().size() : 0;

    return UserResponseDto.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getRole())
        .enabled(user.isEnabled())
        .createdAt(user.getCreatedAt())
        .cardCount(cardCount)
        .build();
  }

  public UserResponseDto toUserResponseDto(User user) {
    return toUserResponseStatic(user);
  }
}
