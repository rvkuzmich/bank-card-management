package com.example.bankcards.service;

import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.BlockRequestResponseDto;
import com.example.bankcards.dto.response.CardResponseDto;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

  CardResponseDto createCard(CardRequestDto request, String username);

  Page<CardResponseDto> getUserCards(String username, CardFilterRequestDto filter,
      Pageable pageable);

  CardResponseDto blockCard(String cardId, String username);

  CardResponseDto activateCard(String cardId, String username);

  BigDecimal getCardBalance(String cardId, String username);

  BlockRequestResponseDto requestCardBlock(String cardId, String username);

  CardResponseDto approveBlockCard(String cardId, String adminUsername);

  CardResponseDto rejectBlockCard(String cardId, String adminUsername);
}
