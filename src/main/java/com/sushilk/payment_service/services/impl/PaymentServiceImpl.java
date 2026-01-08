package com.sushilk.payment_service.services.impl;

import com.sushilk.payment_service.dtos.InitiatePaymentRequest;
import com.sushilk.payment_service.dtos.InitiatePaymentResponse;
import com.sushilk.payment_service.dtos.RefundRequest;
import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.entities.PaymentAttempt;
import com.sushilk.payment_service.entities.PaymentTransaction;
import com.sushilk.payment_service.enums.PaymentStatus;
import com.sushilk.payment_service.enums.TransactionType;
import com.sushilk.payment_service.exceptions.PaymentAlreadyExistsException;
import com.sushilk.payment_service.exceptions.PaymentNotFoundException;
import com.sushilk.payment_service.repositories.PaymentAttemptRepository;
import com.sushilk.payment_service.repositories.PaymentRepository;
import com.sushilk.payment_service.repositories.PaymentTransactionRepository;
import com.sushilk.payment_service.services.PaymentProviderFactory;
import com.sushilk.payment_service.services.PaymentProviderService;
import com.sushilk.payment_service.services.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository attemptRepository;
    private final PaymentProviderFactory providerFactory;
    private final PaymentTransactionRepository transactionRepository;

    @Override
    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest req) {

        // 1️⃣ Idempotency check
        paymentRepository.findByIdempotencyKey(req.idempotencyKey())
                .ifPresent(p -> {
                    throw new PaymentAlreadyExistsException(
                            "Payment already exists for this idempotency key: " + req.idempotencyKey());
                });

        // 2️⃣ Create Payment
        Payment payment = Payment.builder()
                .userId(req.userId())
                .orderId(req.orderId())
                .orderType(req.orderType())
                .amount(req.amount())
                .currency(req.currency())
                .provider(req.provider())
                .paymentMethod(req.paymentMethod())
                .status(PaymentStatus.CREATED)
                .idempotencyKey(req.idempotencyKey())
                .build();

        payment = paymentRepository.save(payment);

        // 3️⃣ Create PaymentAttempt
        PaymentAttempt attempt = PaymentAttempt.builder()
                .paymentId(payment.getPaymentId())
                .provider(req.provider())
                .attemptNo(1)
                .status("CREATED")
                .build();

        attemptRepository.save(attempt);

        // 4️⃣ Call Provider
        PaymentProviderService provider = providerFactory.getProvider(req.provider());
        provider.initiate(payment);

        // 5️⃣ Update Status
        payment.setStatus(PaymentStatus.INITIATED);
        paymentRepository.save(payment);

        return new InitiatePaymentResponse(payment.getPaymentId(), payment.getStatus());
    }

    @Override
    @Transactional
    public void refundPayment(RefundRequest request) {
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + request.paymentId()));

        // 1️⃣ Create refund transaction
        PaymentTransaction txn = PaymentTransaction.builder()
                .payment(payment)
                .transactionType(TransactionType.REFUND)
                .amount(request.amount())
                .status(PaymentStatus.REFUND_INITIATED)
                .description(request.reason())
                .build();

        transactionRepository.save(txn);

        // 2️⃣ Call provider to refund
        PaymentProviderService provider = providerFactory.getProvider(payment.getProvider());
        try {
            provider.refund(payment);
            payment.setStatus(PaymentStatus.REFUNDED);
            txn.setStatus(PaymentStatus.REFUNDED);
        } catch (Exception e) {
            // rollback transaction automatically due to @Transactional
            txn.setStatus(PaymentStatus.FAILED);
            throw new RuntimeException("Refund failed: " + e.getMessage());
        } finally {
            transactionRepository.save(txn);
            paymentRepository.save(payment);
        }
    }

}

