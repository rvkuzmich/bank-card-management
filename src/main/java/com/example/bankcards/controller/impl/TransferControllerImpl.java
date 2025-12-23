package com.example.bankcards.controller.impl;

import com.example.bankcards.controller.TransferController;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.ApiResponse;
import com.example.bankcards.dto.response.TransferResponseDto;
import com.example.bankcards.service.TransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Transfer Management", description = "Money transfer operations")
public class TransferControllerImpl implements TransferController {

  private final TransferService transferService;

  @Override
  public ResponseEntity<ApiResponse<TransferResponseDto>> transfer(
      TransferRequestDto request,
      Principal principal) {

    if (principal == null) {
      throw new BadCredentialsException("User is not authenticated");
    }

    TransferResponseDto response = transferService.transferBetweenOwnCards(
        request, principal.getName());

    return ResponseEntity.ok(ApiResponse.success(response, "Transfer completed successfully"));
  }

  @Override
  public ResponseEntity<ApiResponse<Page<TransferResponseDto>>> getTransferHistory(
      Pageable pageable,
      Principal principal) {

    Page<TransferResponseDto> history = transferService.getTransferHistory(
        principal.getName(), pageable);

    return ResponseEntity.ok(ApiResponse.success(history));
  }
}