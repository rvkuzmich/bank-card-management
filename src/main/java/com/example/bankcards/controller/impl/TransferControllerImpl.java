package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.TransferController;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.ApiResponseDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transfers")
public class TransferControllerImpl implements TransferController {

  private final TransferService transferService;

  @Override
  @PostMapping
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<ApiResponseDto<TransferResponseDto>> transfer(
      @Valid @RequestBody TransferRequestDto request,
      Principal principal) {

    if (principal == null) {
      throw new BadCredentialsException("User is not authenticated");
    }

    TransferResponseDto response = transferService.transferBetweenOwnCards(
        request, principal.getName());

    return ResponseEntity.ok(ApiResponseDto.success(response, "Transfer completed successfully"));
  }

  @Override
  @GetMapping("/history")
  @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
  public ResponseEntity<ApiResponseDto<Page<TransferResponseDto>>> getTransferHistory(
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
      Principal principal) {

    Page<TransferResponseDto> history = transferService.getTransferHistory(
        principal.getName(), pageable);

    return ResponseEntity.ok(ApiResponseDto.success(history, "Transfer history retrieved"));
  }
}
