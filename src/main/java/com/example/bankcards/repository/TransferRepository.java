package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, String> {

  @Query(
      "SELECT t FROM Transfer t WHERE t.fromCard.user.id = :userId OR t.toCard.user.id = :userId " +
          "ORDER BY t.timestamp DESC")
  Page<Transfer> findByUserId(@Param("userId") String userId, Pageable pageable);

  @Query("SELECT t FROM Transfer t WHERE t.fromCard.id = :cardId OR t.toCard.id = :cardId " +
      "ORDER BY t.timestamp DESC")
  Page<Transfer> findByCardId(@Param("cardId") String cardId, Pageable pageable);

  @Query("SELECT SUM(t.amount) FROM Transfer t WHERE t.fromCard.id = :cardId " +
      "AND t.timestamp >= :startDate AND t.timestamp <= :endDate")
  BigDecimal getOutgoingAmountByCardAndDateRange(
      @Param("cardId") String cardId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate);
}
