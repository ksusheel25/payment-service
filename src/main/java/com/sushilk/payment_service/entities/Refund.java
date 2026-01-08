package com.sushilk.payment_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "refunds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID refundId;

    @Column(nullable = false)
    private UUID paymentId;

    private BigDecimal amount;

    private String status;

    private String reason;

    private String providerRefundId;
}

