package com.sushilk.payment_service.enums;

/**
 * Types of transactions/orders supported by the payment service.
 * This enum helps categorize different payment scenarios:
 * - PRODUCT: E-commerce product purchase
 * - SUBSCRIPTION: Recurring subscription payment
 * - WALLET: Wallet top-up or wallet-to-wallet transfer
 * - P2P: Person-to-person direct transfer
 * - BILL_PAYMENT: Utility bill payments (electricity, water, etc.)
 * - DONATION: Donation to organizations/individuals
 */
public enum OrderType {
    PRODUCT,        // E-commerce product purchase (merchant receives payment)
    SUBSCRIPTION,   // Recurring subscription payment (merchant receives payment)
    WALLET,         // Wallet top-up or wallet-to-wallet transfer
    P2P,            // Person-to-person direct transfer (requires beneficiary)
    BILL_PAYMENT,   // Utility bill payments
    DONATION        // Donation payments
}