package com.sushilk.payment_service.dtos;

public record ProviderResponse(
        boolean success,
        String providerTransactionId,
        String message,
        String rawResponse
) {}
