package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.CardController;
import com.example.bankcards.dto.request.CardFilterRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Bank card operations")
public class CardControllerImpl implements CardController {

  private final CardService cardService;

  @Override
  public ResponseEntity<ApiResponse<CardResponse>> createCard(
      CardRequest request,
      Principal principal) {
    CardResponse response = cardService.createCard(request, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  public ResponseEntity<ApiResponse<Page<CardResponse>>> getMyCards(
      CardFilterRequest filter,
      Pageable pageable,
      Principal principal) {
    Page<CardResponse> cards = cardService.getUserCards(principal.getName(), filter, pageable);
    return ResponseEntity.ok(ApiResponse.success(cards));
  }

  @Override
  public ResponseEntity<ApiResponse<CardResponse>> blockCard(
      String cardId,
      Principal principal) {
    CardResponse response = cardService.blockCard(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  public ResponseEntity<ApiResponse<CardResponse>> activateCard(
      String cardId,
      Principal principal) {
    CardResponse response = cardService.activateCard(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  public ResponseEntity<ApiResponse<BigDecimal>> getBalance(
      String cardId,
      Principal principal) {
    BigDecimal balance = cardService.getCardBalance(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(balance));
  }
}
