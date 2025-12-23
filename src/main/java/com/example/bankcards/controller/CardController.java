package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.CardResponseDto;
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
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/cards")
public interface CardController {

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create new card", description = "Admin only")
  ResponseEntity<ApiResponse<CardResponseDto>> createCard(
      @Valid @RequestBody CardRequestDto request, Principal principal);

  @GetMapping()
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get user's cards", description = "With filtering and pagination")
  ResponseEntity<ApiResponse<Page<CardResponseDto>>> getMyCards(
      @Valid CardFilterRequestDto filter, @PageableDefault(size = 10) Pageable pageable,
      Principal principal);

  @PostMapping("/{cardId}/block")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  @Operation(summary = "Block card")
  ResponseEntity<ApiResponse<?>> blockCard(
      @PathVariable String cardId, Principal principal);

  @PostMapping("/{cardId}/approve-block")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponse<CardResponseDto>> approveBlockCard(
      @PathVariable String cardId, @RequestParam(required = false) String username);

  @PostMapping("/{cardId}/activate")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Activate card")
  ResponseEntity<ApiResponse<CardResponseDto>> activateCard(
      @PathVariable String cardId, Principal principal);

  @GetMapping("/{cardId}/balance")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get card balance")
  ResponseEntity<ApiResponse<BigDecimal>> getBalance(
      @PathVariable String cardId, Principal principal);
}
