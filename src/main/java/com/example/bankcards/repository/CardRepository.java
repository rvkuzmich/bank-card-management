package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, String>,
    JpaSpecificationExecutor<Card> {

  Optional<Card> findByIdAndUserId(String id, String userId);

  Optional<Card> findCardById(String cardId);
}
