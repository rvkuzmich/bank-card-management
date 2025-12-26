package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID>,
    JpaSpecificationExecutor<Card> {

  Optional<Card> findByIdAndUserId(UUID id, UUID userId);

  Optional<Card> findCardById(UUID cardId);
}
