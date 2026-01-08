package com.sushilk.payment_service.repositories;

import com.sushilk.payment_service.entities.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, UUID> {

    List<PaymentAttempt> findByPaymentId(UUID paymentId);
}

