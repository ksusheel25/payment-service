package com.sushilk.payment_service.dtos;

import com.sushilk.payment_service.enums.OrderType;
import com.sushilk.payment_service.enums.PaymentMethod;
import com.sushilk.payment_service.enums.PaymentProvider;
import com.sushilk.payment_service.validation.CardDetailsRequired;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@CardDetailsRequired
public record InitiatePaymentRequest(
        @NotBlank(message = "User ID is required and cannot be blank")
        @Size(min = 1, max = 100, message = "User ID must be between 1 and 100 characters")
        String userId,

        @NotBlank(message = "Order ID is required and cannot be blank")
        @Size(min = 1, max = 100, message = "Order ID must be between 1 and 100 characters")
        String orderId,

        @NotNull(message = "Order type is required. Valid values: PRODUCT, SUBSCRIPTION, WALLET")
        OrderType orderType,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "Currency is required and cannot be blank")
        @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code (e.g., USD, INR, EUR)")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter uppercase ISO code")
        String currency,

        @NotNull(message = "Payment provider is required. Valid values: PHONEPE, PAYTM, GOOGLEPAY, CARD")
        PaymentProvider provider,

        @NotNull(message = "Payment method is required. Valid values: UPI, CARD, NET_BANKING")
        PaymentMethod paymentMethod,

        @NotBlank(message = "Idempotency key is required and cannot be blank")
        @Size(min = 1, max = 255, message = "Idempotency key must be between 1 and 255 characters")
        String idempotencyKey,

        /**
         * Card details - required only when paymentMethod is CARD
         * Should be validated using @Valid to trigger nested validation
         */
        @Valid
        CardDetails cardDetails
) {}

