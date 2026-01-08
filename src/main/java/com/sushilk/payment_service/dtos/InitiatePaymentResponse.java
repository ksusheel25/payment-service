package com.sushilk.payment_service.dtos;

import com.sushilk.payment_service.enums.PaymentStatus;

import java.util.UUID;

public record InitiatePaymentResponse(
        UUID paymentId,
        PaymentStatus status
) {}


