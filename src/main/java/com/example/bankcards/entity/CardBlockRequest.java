package com.example.bankcards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_block_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBlockRequest {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "card_id")
  private Card card;

  @Column(name = "requested_by")
  private String requestedBy;

  @Column(name = "requested_at")
  private LocalDateTime requestedAt;

  @Column(name = "approved_by")
  private String approvedBy;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Enumerated(EnumType.STRING)
  private RequestStatus status;
}
