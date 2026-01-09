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
public class GooglePayPaymentProvider implements PaymentProviderService {

    private final ObjectMapper objectMapper;

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.GOOGLEPAY;
    }

    @Override
    public ProviderResponse initiatePayment(Payment payment) {
        log.info("Initiating GOOGLEPAY payment for: {}", payment.getPaymentId());
        
        try {
            String providerTxnId = "GPay_TXN_" + UUID.randomUUID();
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
        log.info("Processing GOOGLEPAY refund for payment: {}, reason: {}", payment.getPaymentId(), reason);
        
        try {
            String providerRefundId = "GPay_REFUND_" + UUID.randomUUID();
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

