package com.sushilk.payment_service.services.impl;

import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.enums.PaymentProvider;
import com.sushilk.payment_service.services.PaymentProviderService;
import org.springframework.stereotype.Service;

@Service
public class PhonePePaymentProvider implements PaymentProviderService {

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.PHONEPE;
    }

    @Override
    public void initiate(Payment payment) {
        System.out.println("Initiating PHONEPE payment for: " + payment.getPaymentId());
        // TODO
    }

    @Override
    public void refund(Payment payment) {
        System.out.println("Refund PHONEPE payment: " + payment.getPaymentId());
        // TODO
    }
}

