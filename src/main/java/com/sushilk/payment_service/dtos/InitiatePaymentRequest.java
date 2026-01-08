package com.sushilk.payment_service.dtos;

import com.sushilk.payment_service.enums.OrderType;
import com.sushilk.payment_service.enums.PaymentMethod;
import com.sushilk.payment_service.enums.PaymentProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record InitiatePaymentRequest(
        @NotBlank String userId,
        @NotBlank String orderId,
        @NotNull OrderType orderType,
        @NotNull BigDecimal amount,
        @NotBlank String currency,
        @NotNull PaymentProvider provider,
        @NotNull PaymentMethod paymentMethod,
        @NotBlank String idempotencyKey
) {}

