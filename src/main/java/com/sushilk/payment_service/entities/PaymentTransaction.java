package com.sushilk.payment_service.entities;

import com.sushilk.payment_service.enums.PaymentStatus;
import com.sushilk.payment_service.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_transactions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PaymentTransaction extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID transactionId;  // ✅ unique identifier for this entity

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String description;
}
