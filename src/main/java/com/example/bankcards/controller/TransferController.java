package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/transfers")
public interface TransferController {

  @PostMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Transfer between cards", description = "Transfer money between user's own cards")
  ResponseEntity<ApiResponse<TransferResponse>> transfer(
      @Valid @RequestBody TransferRequest request,
      Principal principal);

  @GetMapping("/history")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  @Operation(summary = "Get transfer history", description = "Get user's transfer history")
  ResponseEntity<ApiResponse<Page<TransferResponse>>> getTransferHistory(
      @PageableDefault(size = 20) Pageable pageable,
      Principal principal);
}
