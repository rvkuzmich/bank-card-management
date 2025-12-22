package com.example.bankcards.service;

import com.example.bankcards.dto.request.CardFilterRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.CardResponse;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {

  CardResponse createCard(CardRequest request, String username);

  Page<CardResponse> getUserCards(String username, CardFilterRequest filter, Pageable pageable);

  CardResponse blockCard(String cardId, String username);

  CardResponse activateCard(String cardId, String username);

  BigDecimal getCardBalance(String cardId, String username);
}
