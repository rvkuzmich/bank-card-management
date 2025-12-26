package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, UUID> {

  @Query(
      "SELECT t FROM Transfer t WHERE t.fromCard.user.id = :userId OR t.toCard.user.id = :userId " +
          "ORDER BY t.timestamp DESC")
  Page<Transfer> findByUserId(@Param("userId") UUID userId, Pageable pageable);

  @Query("SELECT t FROM Transfer t WHERE t.fromCard.id = :cardId OR t.toCard.id = :cardId " +
      "ORDER BY t.timestamp DESC")
  Page<Transfer> findByCardId(@Param("cardId") UUID cardId, Pageable pageable);
}
