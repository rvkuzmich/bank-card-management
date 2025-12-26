package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.CardController;
import com.example.bankcards.dto.request.CardFilterRequestDto;
import com.example.bankcards.dto.request.CardRequestDto;
import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.BlockRequestResponseDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
@Validated
public class CardControllerImpl implements CardController {

  private final CardService cardService;

  @Override
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponseDto<CardResponseDto>> createCard(
      @Valid @RequestBody CardRequestDto request,
      Principal principal) {
    CardResponseDto response = cardService.createCard(request, principal.getName());
    return ResponseEntity.ok(ApiResponseDto.success(response, "Card created successfully"));
  }

  @Override
  @GetMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<ApiResponseDto<Page<CardResponseDto>>> getMyCards(
      @Valid CardFilterRequestDto filter,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      Principal principal) {
    if (principal == null) {
      throw new AuthenticationCredentialsNotFoundException("Authentication required");
    }
    Page<CardResponseDto> cards = cardService.getUserCards(principal.getName(), filter, pageable);
    return ResponseEntity.ok(ApiResponseDto.success(cards));
  }

  @Override
  @PostMapping("/{cardId}/block")
  @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
  public ResponseEntity<ApiResponseDto<?>> blockCard(
      @PathVariable String cardId,
      Principal principal) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication.getAuthorities().stream()
        .anyMatch(grantedAuthority -> Objects.equals(grantedAuthority
            .getAuthority(), "ROLE_ADMIN"))) {
      CardResponseDto response = cardService.blockCard(cardId, principal.getName());
      return ResponseEntity.ok(ApiResponseDto.success(response, "Card blocked immediately"));
    } else {
      BlockRequestResponseDto response = cardService.requestCardBlock(cardId, principal.getName());
      return ResponseEntity.ok(ApiResponseDto.success(response, "Block request created"));
    }
  }

  @Override
  @PostMapping("/{cardId}/approve-block")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponseDto<CardResponseDto>> approveBlockCard(
      @PathVariable String cardId,
      @RequestParam(required = false) String username) {

    CardResponseDto response = cardService.approveBlockCard(cardId, username);
    return ResponseEntity.ok(ApiResponseDto.success(response, "Block request approved"));
  }

  @Override
  @PostMapping("/{cardId}/activate")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ApiResponseDto<CardResponseDto>> activateCard(
      @PathVariable String cardId,
      Principal principal) {
    CardResponseDto response = cardService.activateCard(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponseDto.success(response, "Card activated"));
  }

  @Override
  @GetMapping("/{cardId}/balance")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<ApiResponseDto<BigDecimal>> getBalance(
      @PathVariable String cardId,
      Principal principal) {
    BigDecimal balance = cardService.getCardBalance(cardId, principal.getName());
    return ResponseEntity.ok(ApiResponseDto.success(balance, "Balance retrieved"));
  }
}
