package com.sushilk.payment_service.services;

import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.enums.PaymentProvider;

public interface PaymentProviderService {

    PaymentProvider getProvider();

    void initiate(Payment payment);

    void refund(Payment payment);
}

