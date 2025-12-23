package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.CardController;
import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.BlockRequestResponseDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Bank card operations")
@Validated
public class CardControllerImpl implements CardController {

  private final CardService cardService;

  @Override
  public ResponseEntity<ApiResponse<CardResponseDto>> createCard(
      CardRequestDto request, Principal principal) {
    CardResponseDto response = cardService.createCard(request, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  public ResponseEntity<ApiResponse<Page<CardResponseDto>>> getMyCards(
      CardFilterRequestDto filter, Pageable pageable, Principal principal) {
    if (principal == null) {
      throw new AuthenticationCredentialsNotFoundException("Authentication required");
    }
    Page<CardResponseDto> cards = cardService.getUserCards(principal.getName(), filter, pageable);
    return ResponseEntity.ok(ApiResponse.success(cards));
  }

  @Override
  public ResponseEntity<ApiResponse<?>> blockCard(String cardId, Principal principal) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> Objects.equals(grantedAuthority
            .getAuthority(), "ROLE_ADMIN"))) {
      CardResponseDto response = cardService.blockCard(cardId, principal.getName());
      return ResponseEntity.ok(ApiResponse.success(response));
    } else {
      BlockRequestResponseDto response = cardService.requestCardBlock(cardId, principal.getName());
      return ResponseEntity.ok(ApiResponse.success(response));
    }
  }

  @Override
  public ResponseEntity<ApiResponse<CardResponseDto>> approveBlockCard(
      String cardId, String username) {

    CardResponseDto response = cardService.approveBlockCard(cardId, username);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  public ResponseEntity<ApiResponse<CardResponseDto>> activateCard(
      String cardId, Principal principal) {
    CardResponseDto response = cardService.activateCard(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Override
  public ResponseEntity<ApiResponse<BigDecimal>> getBalance(
      String cardId, Principal principal) {
    BigDecimal balance = cardService.getCardBalance(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(balance));
  }
}
