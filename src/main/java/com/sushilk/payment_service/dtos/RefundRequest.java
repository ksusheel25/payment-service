package com.sushilk.payment_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundRequest(
        UUID paymentId,
        @NotNull BigDecimal amount,
        @NotBlank String reason
) {}


