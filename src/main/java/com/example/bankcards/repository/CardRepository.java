package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, String>,
    JpaSpecificationExecutor<Card> {

  Optional<Card> findByIdAndUserId(String id, String userId);

  List<Card> findByUserId(String userId);

  Page<Card> findByUserId(String userId, Pageable pageable);

  Optional<Card> findByCardNumber(String cardNumber);

  Optional<Card> findCardById(String cardId);

  @Query("SELECT c FROM Card c WHERE c.user.id = :userId AND c.status = 'ACTIVE'")
  List<Card> findActiveCardsByUserId(@Param("userId") String userId);

  @Query("SELECT COUNT(c) FROM Card c WHERE c.user.id = :userId")
  long countByUserId(@Param("userId") String userId);
}