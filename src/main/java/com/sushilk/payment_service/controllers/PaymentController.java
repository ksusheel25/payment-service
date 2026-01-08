package com.sushilk.payment_service.controllers;

import com.sushilk.payment_service.dtos.InitiatePaymentRequest;
import com.sushilk.payment_service.dtos.InitiatePaymentResponse;
import com.sushilk.payment_service.dtos.RefundRequest;
import com.sushilk.payment_service.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<InitiatePaymentResponse> initiatePayment(
            @Valid @RequestBody InitiatePaymentRequest request) {
        log.info("InitiatePayment request for user: {}", request.userId());
        InitiatePaymentResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refund")
    public ResponseEntity<Void> refundPayment(
            @Valid @RequestBody RefundRequest request) {
        log.info("RefundPayment request for paymentId: {}", request.paymentId());
        paymentService.refundPayment(request);
        return ResponseEntity.ok().build();
    }
}

