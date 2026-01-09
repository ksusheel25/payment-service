package com.sushilk.payment_service.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.sushilk.payment_service.dtos.InitiatePaymentRequest;
import com.sushilk.payment_service.dtos.InitiatePaymentResponse;
import com.sushilk.payment_service.dtos.ProviderResponse;
import com.sushilk.payment_service.dtos.RefundRequest;
import com.sushilk.payment_service.entities.Payment;
import com.sushilk.payment_service.entities.PaymentAttempt;
import com.sushilk.payment_service.entities.PaymentTransaction;
import com.sushilk.payment_service.entities.Refund;
import com.sushilk.payment_service.enums.PaymentAttemptStatus;
import com.sushilk.payment_service.enums.PaymentStatus;
import com.sushilk.payment_service.enums.RefundStatus;
import com.sushilk.payment_service.enums.TransactionStatus;
import com.sushilk.payment_service.enums.TransactionType;
import com.sushilk.payment_service.exceptions.PaymentNotFoundException;
import com.sushilk.payment_service.repositories.PaymentAttemptRepository;
import com.sushilk.payment_service.repositories.PaymentRepository;
import com.sushilk.payment_service.repositories.PaymentTransactionRepository;
import com.sushilk.payment_service.repositories.RefundRepository;
import com.sushilk.payment_service.services.PaymentProviderFactory;
import com.sushilk.payment_service.services.PaymentProviderService;
import com.sushilk.payment_service.services.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository attemptRepository;
    private final PaymentProviderFactory providerFactory;
    private final PaymentTransactionRepository transactionRepository;
    private final RefundRepository refundRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public InitiatePaymentResponse initiatePayment(InitiatePaymentRequest req) {
        log.info("Initiating payment for user: {}, orderId: {}, idempotencyKey: {}", 
                req.userId(), req.orderId(), req.idempotencyKey());

        // 1️⃣ Idempotency check - return existing payment if found
        Payment existingPayment = paymentRepository.findByIdempotencyKey(req.idempotencyKey())
                .orElse(null);
        if (existingPayment != null) {
            log.info("Payment already exists for idempotencyKey: {}, returning existing payment: {}", 
                    req.idempotencyKey(), existingPayment.getPaymentId());
            return new InitiatePaymentResponse(existingPayment.getPaymentId(), existingPayment.getStatus());
        }

        // 2️⃣ Create Payment (CREATED status)
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
        log.info("Created payment with id: {}", payment.getPaymentId());

        try {
            // 3️⃣ Create PaymentAttempt (INITIATED status)
            List<PaymentAttempt> previousAttempts = attemptRepository.findByPaymentId(payment.getPaymentId());
            int attemptNo = previousAttempts.size() + 1;

            // Log card details safely (masked) if present
            if (req.cardDetails() != null) {
                log.info("Processing card payment with masked card: {}", req.cardDetails().getMaskedCardNumber());
            }

            PaymentAttempt attempt = PaymentAttempt.builder()
                    .paymentId(payment.getPaymentId())
                    .provider(req.provider())
                    .attemptNo(attemptNo)
                    .status(PaymentAttemptStatus.INITIATED)
                    .requestPayload(serializeRequest(req)) // This will mask card details
                    .build();
            attempt = attemptRepository.save(attempt);
            log.info("Created payment attempt {} for payment: {}", attemptNo, payment.getPaymentId());

            // 4️⃣ Create PaymentTransaction (DEBIT, INITIATED)
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentId(payment.getPaymentId())
                    .transactionType(TransactionType.DEBIT)
                    .amount(req.amount())
                    .status(TransactionStatus.INITIATED)
                    .description("Payment initiation for order: " + req.orderId())
                    .build();
            transaction = transactionRepository.save(transaction);
            log.info("Created transaction: {} for payment: {}", transaction.getTransactionId(), payment.getPaymentId());

            // 5️⃣ Update Payment status to INITIATED
            payment.setStatus(PaymentStatus.INITIATED);
            payment = paymentRepository.save(payment);

            // 6️⃣ Call Provider
            PaymentProviderService provider = providerFactory.getProvider(req.provider());
            ProviderResponse providerResponse = provider.initiatePayment(payment);

            // 7️⃣ Update Attempt with provider response
            attempt.setResponsePayload(providerResponse.rawResponse());
            if (providerResponse.success()) {
                attempt.setStatus(PaymentAttemptStatus.SUCCESS);
                payment.setStatus(PaymentStatus.PROCESSING);
                transaction.setStatus(TransactionStatus.SUCCESS);
                log.info("Provider call successful for payment: {}", payment.getPaymentId());
            } else {
                attempt.setStatus(PaymentAttemptStatus.FAILED);
                payment.setStatus(PaymentStatus.FAILED);
                transaction.setStatus(TransactionStatus.FAILED);
                log.warn("Provider call failed for payment: {}", payment.getPaymentId());
            }

            attemptRepository.save(attempt);
            transactionRepository.save(transaction);
            payment = paymentRepository.save(payment);

            // Note: In real implementation, payment status would be updated to SUCCESS/FAILED
            // based on webhook confirmation from provider. For now, we keep it as PROCESSING on success.

            return new InitiatePaymentResponse(payment.getPaymentId(), payment.getStatus());

        } catch (Exception e) {
            log.error("Error during payment initiation for payment: {}", payment.getPaymentId(), e);
            // Rollback - mark payment and transaction as failed
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            
            // Update last attempt and transaction if they exist
            List<PaymentAttempt> attempts = attemptRepository.findByPaymentId(payment.getPaymentId());
            if (!attempts.isEmpty()) {
                PaymentAttempt lastAttempt = attempts.get(attempts.size() - 1);
                lastAttempt.setStatus(PaymentAttemptStatus.FAILED);
                attemptRepository.save(lastAttempt);
            }
            
            List<PaymentTransaction> transactions = transactionRepository.findByPaymentId(payment.getPaymentId());
            if (!transactions.isEmpty()) {
                PaymentTransaction lastTxn = transactions.get(transactions.size() - 1);
                lastTxn.setStatus(TransactionStatus.FAILED);
                transactionRepository.save(lastTxn);
            }
            
            throw new RuntimeException("Payment initiation failed: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void refundPayment(RefundRequest request) {
        log.info("Processing refund request for paymentId: {}, amount: {}", 
                request.paymentId(), request.amount());

        // 1️⃣ Validate payment exists and is refundable
        Payment payment = paymentRepository.findById(request.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + request.paymentId()));

        if (payment.getStatus() != PaymentStatus.SUCCESS && payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new IllegalStateException(
                    String.format("Payment with status '%s' cannot be refunded. Only payments with status SUCCESS or PROCESSING can be refunded.", 
                            payment.getStatus()));
        }

        // Validate refund amount doesn't exceed payment amount
        if (request.amount().compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    String.format("Refund amount (%.2f) cannot exceed the payment amount (%.2f)", 
                            request.amount(), payment.getAmount()));
        }

        // Validate refund amount doesn't exceed remaining refundable amount
        List<Refund> existingRefunds = refundRepository.findByPaymentId(payment.getPaymentId());
        BigDecimal totalRefunded = existingRefunds.stream()
                .filter(r -> r.getStatus() == RefundStatus.SUCCESS)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingRefundable = payment.getAmount().subtract(totalRefunded);
        if (request.amount().compareTo(remainingRefundable) > 0) {
            throw new IllegalArgumentException(
                    String.format("Refund amount (%.2f) cannot exceed the remaining refundable amount (%.2f). Total already refunded: %.2f", 
                            request.amount(), remainingRefundable, totalRefunded));
        }

        // 2️⃣ Store original payment status before modifying it
        PaymentStatus originalPaymentStatus = payment.getStatus();

        // 3️⃣ Create Refund entry (INITIATED)
        Refund refund = Refund.builder()
                .paymentId(payment.getPaymentId())
                .amount(request.amount())
                .status(RefundStatus.INITIATED)
                .reason(request.reason())
                .build();
        refund = refundRepository.save(refund);
        log.info("Created refund: {} for payment: {}", refund.getRefundId(), payment.getPaymentId());

        try {
            // 4️⃣ Create PaymentTransaction (REFUND, INITIATED)
            PaymentTransaction transaction = PaymentTransaction.builder()
                    .paymentId(payment.getPaymentId())
                    .transactionType(TransactionType.REFUND)
                    .amount(request.amount())
                    .status(TransactionStatus.INITIATED)
                    .description("Refund: " + request.reason())
                    .build();
            transaction = transactionRepository.save(transaction);
            log.info("Created refund transaction: {} for payment: {}", 
                    transaction.getTransactionId(), payment.getPaymentId());

            // 5️⃣ Update Payment status to REFUND_INITIATED
            payment.setStatus(PaymentStatus.REFUND_INITIATED);
            payment = paymentRepository.save(payment);

            // 6️⃣ Call provider to refund
            PaymentProviderService provider = providerFactory.getProvider(payment.getProvider());
            ProviderResponse providerResponse = provider.refundPayment(payment, request.reason());

            // 7️⃣ Update refund, transaction, and payment status based on provider response
            refund.setProviderRefundId(providerResponse.providerTransactionId());
            if (providerResponse.success()) {
                refund.setStatus(RefundStatus.SUCCESS);
                transaction.setStatus(TransactionStatus.SUCCESS);
                payment.setStatus(PaymentStatus.REFUNDED);
                log.info("Refund successful for payment: {}", payment.getPaymentId());
            } else {
                refund.setStatus(RefundStatus.FAILED);
                transaction.setStatus(TransactionStatus.FAILED);
                // Revert payment status to original state before refund attempt
                payment.setStatus(originalPaymentStatus);
                log.warn("Refund failed for payment: {}, reverting to original status: {}", 
                        payment.getPaymentId(), originalPaymentStatus);
            }

            refundRepository.save(refund);
            transactionRepository.save(transaction);
            paymentRepository.save(payment);

        } catch (Exception e) {
            log.error("Error during refund for payment: {}", payment.getPaymentId(), e);
            // Rollback - mark refund and transaction as failed
            refund.setStatus(RefundStatus.FAILED);
            refundRepository.save(refund);

            // Revert payment status to original state before refund attempt
            payment.setStatus(originalPaymentStatus);
            paymentRepository.save(payment);
            log.info("Reverted payment {} status to original: {} due to exception", 
                    payment.getPaymentId(), originalPaymentStatus);

            throw new RuntimeException("Refund failed: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private String serializeRequest(InitiatePaymentRequest req) {
        try {
            // Convert to Map for easy modification
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(Map.class, String.class, Object.class);
            Map<String, Object> requestMap = objectMapper.convertValue(req, mapType);
            
            // For PCI compliance: Mask sensitive card details before storing
            if (req.cardDetails() != null && requestMap.containsKey("cardDetails")) {
                Object cardDetailsObj = requestMap.get("cardDetails");
                if (cardDetailsObj instanceof Map) {
                    Map<String, Object> cardDetailsMap = (Map<String, Object>) cardDetailsObj;
                    // Mask sensitive card information
                    cardDetailsMap.put("cardNumber", req.cardDetails().getMaskedCardNumber());
                    cardDetailsMap.put("cvv", "***");
                    cardDetailsMap.put("expiryDate", "**/**");
                    // Keep cardholderName as it's not considered sensitive for audit purposes
                }
            }
            
            return objectMapper.writeValueAsString(requestMap);
        } catch (Exception e) {
            log.warn("Failed to serialize request payload", e);
            return "{}";
        }
    }
}

