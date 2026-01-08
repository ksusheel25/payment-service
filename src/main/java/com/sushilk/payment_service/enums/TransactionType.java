package com.sushilk.payment_service.enums;

public enum TransactionType {
    DEBIT,      // Payment deducted
    CREDIT,     // Payment added (e.g., wallet top-up)
    REFUND      // Refund issued
}

