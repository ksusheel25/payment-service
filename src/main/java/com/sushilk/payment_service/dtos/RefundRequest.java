package com.sushilk.payment_service.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundRequest(
        @NotNull(message = "Payment ID is required and cannot be null")
        UUID paymentId,

        @NotNull(message = "Refund amount is required")
        @Positive(message = "Refund amount must be greater than zero")
        @DecimalMin(value = "0.01", message = "Refund amount must be at least 0.01")
        BigDecimal amount,

        @NotBlank(message = "Refund reason is required and cannot be blank")
        @Size(min = 5, max = 500, message = "Refund reason must be between 5 and 500 characters")
        String reason
) {}


