package com.sushilk.payment_service.entities;

import com.sushilk.payment_service.enums.PaymentProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "payment_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAttempt extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID attemptId;

    @Column(nullable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    private PaymentProvider provider;

    private Integer attemptNo;

    private String status;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String requestPayload;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String responsePayload;
}
