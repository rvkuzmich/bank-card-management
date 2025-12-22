package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.dto.response.TransferResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferService {

  TransferResponse transferBetweenOwnCards(TransferRequest request, String username);

  Page<TransferResponse> getTransferHistory(String username, Pageable pageable);

  Page<TransferResponse> getCardTransferHistory(String cardId, String username, Pageable pageable);
}
