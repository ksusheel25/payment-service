package com.sushilk.payment_service.services.impl;

import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.enums.PaymentProvider;
import com.sushilk.payment_service.services.PaymentProviderService;
import org.springframework.stereotype.Service;

@Service
public class PaytmPaymentProvider implements PaymentProviderService {

    @Override
    public PaymentProvider getProvider() {
        return PaymentProvider.PAYTM;
    }

    @Override
    public void initiate(Payment payment) {
        System.out.println("Initiating PAYTM payment for: " + payment.getPaymentId());
        // TODO
    }

    @Override
    public void refund(Payment payment) {
        System.out.println("Refund PAYTM payment: " + payment.getPaymentId());
        // TODO
    }
}

