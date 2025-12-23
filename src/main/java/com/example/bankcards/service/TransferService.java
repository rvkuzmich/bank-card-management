package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.TransferResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferService {

  TransferResponseDto transferBetweenOwnCards(TransferRequestDto request, String username);

  Page<TransferResponseDto> getTransferHistory(String username, Pageable pageable);

  Page<TransferResponseDto> getCardTransferHistory(String cardId, String username,
      Pageable pageable);
}
