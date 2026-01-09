package com.sushilk.payment_service.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sushilk.payment_service.dtos.ProviderResponse;
import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.enums.PaymentProvider;
import com.sushilk.payment_service.services.PaymentProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhonePePaymentProvider implements PaymentProviderService {

    private final ObjectMapper objectMapper;

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.PHONEPE;
    }

    @Override
    public ProviderResponse initiatePayment(Payment payment) {
        log.info("Initiating PHONEPE payment for: {}", payment.getPaymentId());
        
        // Mock provider call - In production, this would call PhonePe API
        try {
            String providerTxnId = "PHONEPE_TXN_" + UUID.randomUUID();
            Map<String, Object> mockResponse = Map.of(
                    "success", true,
                    "transactionId", providerTxnId,
                    "status", "INITIATED",
                    "message", "Payment initiated successfully"
            );
            
            return new ProviderResponse(
                    true,
                    providerTxnId,
                    "Payment initiated successfully",
                    objectMapper.writeValueAsString(mockResponse)
            );
        } catch (JsonProcessingException e) {
            log.error("Error serializing response", e);
            return new ProviderResponse(false, null, "Provider error", null);
        }
    }

    @Override
    public ProviderResponse refundPayment(Payment payment, String reason) {
        log.info("Processing PHONEPE refund for payment: {}, reason: {}", payment.getPaymentId(), reason);
        
        // Mock provider refund call
        try {
            String providerRefundId = "PHONEPE_REFUND_" + UUID.randomUUID();
            Map<String, Object> mockResponse = Map.of(
                    "success", true,
                    "refundId", providerRefundId,
                    "status", "INITIATED",
                    "message", "Refund initiated successfully"
            );
            
            return new ProviderResponse(
                    true,
                    providerRefundId,
                    "Refund initiated successfully",
                    objectMapper.writeValueAsString(mockResponse)
            );
        } catch (JsonProcessingException e) {
            log.error("Error serializing refund response", e);
            return new ProviderResponse(false, null, "Refund failed", null);
        }
    }
}

