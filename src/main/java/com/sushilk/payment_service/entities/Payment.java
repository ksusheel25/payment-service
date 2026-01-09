package com.sushilk.payment_service.entities;

import com.sushilk.payment_service.enums.OrderType;
import com.sushilk.payment_service.enums.PaymentMethod;
import com.sushilk.payment_service.enums.PaymentProvider;
import com.sushilk.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_order", columnList = "orderId"),
                @Index(name = "idx_payment_user", columnList = "userId"),
                @Index(name = "idx_payment_idempotency", columnList = "idempotencyKey")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue
    private UUID paymentId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, unique = true)
    private String idempotencyKey;

    /**
     * Beneficiary ID - recipient of the payment
     * Required for P2P, BILL_PAYMENT, DONATION
     * Optional for PRODUCT, SUBSCRIPTION (merchant ID can be derived from orderId)
     */
    @Column(length = 100)
    private String beneficiaryId;

    /**
     * Beneficiary name - for display/receipt purposes
     */
    @Column(length = 200)
    private String beneficiaryName;

    /**
     * Beneficiary type - USER, MERCHANT, BANK_ACCOUNT, WALLET, etc.
     */
    @Column(length = 50)
    private String beneficiaryType;

    /**
     * Beneficiary account/UPI ID for direct transfers
     */
    @Column(length = 256)
    private String beneficiaryAccount;
}

