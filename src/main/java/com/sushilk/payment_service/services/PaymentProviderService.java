package com.sushilk.payment_service.services;

import com.sushilk.payment_service.dtos.ProviderResponse;
import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.enums.PaymentProvider;

public interface PaymentProviderService {

    PaymentProvider getProvider();

    ProviderResponse initiatePayment(Payment payment);

    ProviderResponse refundPayment(Payment payment, String reason);
}

