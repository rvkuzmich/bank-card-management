package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.RequestStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBlockRequestRepository extends JpaRepository<CardBlockRequest, UUID> {

  Optional<CardBlockRequest> findByCardIdAndStatus(String cardId, RequestStatus status);

  List<CardBlockRequest> findByStatus(RequestStatus status);
}
