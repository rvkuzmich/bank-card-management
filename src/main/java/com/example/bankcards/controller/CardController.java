package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardFilterRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.CardResponse;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "Bank card operations")
public class CardController {

  private final CardService cardService;

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create new card", description = "Admin only")
  public ResponseEntity<ApiResponse<CardResponse>> createCard(
      @Valid @RequestBody CardRequest request,
      Principal principal) {
    CardResponse response = cardService.createCard(request, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/my")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get user's cards", description = "With filtering and pagination")
  public ResponseEntity<ApiResponse<Page<CardResponse>>> getMyCards(
      @Valid CardFilterRequest filter,
      @PageableDefault(size = 10) Pageable pageable,
      Principal principal) {
    Page<CardResponse> cards = cardService.getUserCards(principal.getName(), filter, pageable);
    return ResponseEntity.ok(ApiResponse.success(cards));
  }

  @PostMapping("/{cardId}/block")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Block card")
  public ResponseEntity<ApiResponse<CardResponse>> blockCard(
      @PathVariable String cardId,
      Principal principal) {
    CardResponse response = cardService.blockCard(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PostMapping("/{cardId}/activate")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Activate card")
  public ResponseEntity<ApiResponse<CardResponse>> activateCard(
      @PathVariable String cardId,
      Principal principal) {
    CardResponse response = cardService.activateCard(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/{cardId}/balance")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get card balance")
  public ResponseEntity<ApiResponse<BigDecimal>> getBalance(
      @PathVariable String cardId,
      Principal principal) {
    BigDecimal balance = cardService.getCardBalance(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponse.success(balance));
  }
}
