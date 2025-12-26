package com.example.bankcards.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "from_card_id", nullable = false)
  @JsonIgnore
  private Card fromCard;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "to_card_id", nullable = false)
  @JsonIgnore
  private Card toCard;

  @Column(nullable = false)
  private BigDecimal amount;

  private String description;

  @Column(nullable = false)
  private LocalDateTime timestamp;
}
