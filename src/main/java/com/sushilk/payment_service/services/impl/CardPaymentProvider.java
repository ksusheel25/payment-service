package com.sushilk.payment_service.services.impl;

import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.enums.PaymentProvider;
import com.sushilk.payment_service.services.PaymentProviderService;
import org.springframework.stereotype.Service;

@Service
public class CardPaymentProvider implements PaymentProviderService {

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.CARD;
    }

    @Override
    public void initiate(Payment payment) {
        System.out.println("Initiating CARD payment for: " + payment.getPaymentId());
        // TODO: integrate with card gateway
    }

    @Override
    public void refund(Payment payment) {
        System.out.println("Refund CARD payment: " + payment.getPaymentId());
        // TODO
    }
}



