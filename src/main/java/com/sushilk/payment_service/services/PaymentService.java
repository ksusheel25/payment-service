package com.sushilk.payment_service.services;

import com.sushilk.payment_service.dtos.InitiatePaymentRequest;
import com.sushilk.payment_service.dtos.InitiatePaymentResponse;
import com.sushilk.payment_service.dtos.RefundRequest;

public interface PaymentService {

    InitiatePaymentResponse initiatePayment(InitiatePaymentRequest request);

    void refundPayment(RefundRequest request);
}

