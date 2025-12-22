package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.TransferResponse;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Transfer Management", description = "Money transfer operations")
public class TransferController {

  private final TransferService transferService;

  @PostMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Transfer between cards", description = "Transfer money between user's own cards")
  public ResponseEntity<ApiResponse<TransferResponse>> transfer(
      @Valid @RequestBody TransferRequest request,
      Principal principal) {

    TransferResponse response = transferService.transferBetweenOwnCards(
        request, principal.getName());

    return ResponseEntity.ok(ApiResponse.success(response, "Transfer completed successfully"));
  }

  @GetMapping("/history")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get transfer history", description = "Get user's transfer history")
  public ResponseEntity<ApiResponse<Page<TransferResponse>>> getTransferHistory(
      @PageableDefault(size = 20) Pageable pageable,
      Principal principal) {

    Page<TransferResponse> history = transferService.getTransferHistory(
        principal.getName(), pageable);

    return ResponseEntity.ok(ApiResponse.success(history));
  }
}