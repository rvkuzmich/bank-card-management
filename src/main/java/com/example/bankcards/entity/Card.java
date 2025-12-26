package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "card_number", nullable = false, unique = true)
  private String cardNumber;

  @Column(name = "masked_number", nullable = false)
  private String maskedNumber;

  @Column(nullable = false)
  private String cardholder;

  @Column(name = "expiry_date", nullable = false)
  private LocalDate expiryDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CardStatus status;

  @Column(nullable = false)
  private BigDecimal balance;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "blocked_at")
  private LocalDateTime blockedAt;

  @Column(name = "blocked_by")
  private String blockedBy;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  public boolean isActive() {
    return status == CardStatus.ACTIVE && !isExpired();
  }

  public boolean isExpired() {
    return expiryDate.isBefore(LocalDate.now());
  }

  public void updateStatus() {
    if (isExpired()) {
      this.status = CardStatus.EXPIRED;
    }
  }
}
