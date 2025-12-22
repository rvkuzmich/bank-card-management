package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardFilterRequest;
import com.example.bankcards.dto.request.CardRequest;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.CardResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
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

@RequestMapping("/api/cards")
public interface CardController {

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create new card", description = "Admin only")
  ResponseEntity<ApiResponse<CardResponse>> createCard(
      @Valid @RequestBody CardRequest request,
      Principal principal);

  @GetMapping("/my")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get user's cards", description = "With filtering and pagination")
  ResponseEntity<ApiResponse<Page<CardResponse>>> getMyCards(
      @Valid CardFilterRequest filter,
      @PageableDefault(size = 10) Pageable pageable,
      Principal principal);

  @PostMapping("/{cardId}/block")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Block card")
  ResponseEntity<ApiResponse<CardResponse>> blockCard(
      @PathVariable String cardId,
      Principal principal);

  @PostMapping("/{cardId}/activate")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Activate card")
  ResponseEntity<ApiResponse<CardResponse>> activateCard(
      @PathVariable String cardId,
      Principal principal);

  @GetMapping("/{cardId}/balance")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get card balance")
  ResponseEntity<ApiResponse<BigDecimal>> getBalance(
      @PathVariable String cardId,
      Principal principal);
}
