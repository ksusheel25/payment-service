package com.sushilk.payment_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Beneficiary/Recipient details for payments.
 * Required for P2P (person-to-person) transactions.
 * Optional for e-commerce (merchant details can be derived from orderId).
 */
public record BeneficiaryDetails(
        /**
         * Beneficiary identifier (can be userId, merchantId, account number, etc.)
         * Required for P2P transfers and direct payments
         */
        @NotBlank(message = "Beneficiary ID is required")
        @Size(min = 1, max = 100, message = "Beneficiary ID must be between 1 and 100 characters")
        String beneficiaryId,

        /**
         * Beneficiary name for display/receipt purposes
         */
        @Size(max = 200, message = "Beneficiary name must not exceed 200 characters")
        String beneficiaryName,

        /**
         * Beneficiary type: USER, MERCHANT, BANK_ACCOUNT, WALLET, etc.
         */
        @Size(max = 50, message = "Beneficiary type must not exceed 50 characters")
        String beneficiaryType,

        /**
         * Beneficiary account/UPI ID (for direct transfers)
         * Format validation depends on beneficiaryType
         */
        @Size(max = 256, message = "Beneficiary account/UPI ID must not exceed 256 characters")
        String beneficiaryAccount,

        /**
         * Optional: IFSC code for bank account transfers
         * Format: 11 characters (4 letter bank code + 0 + 6 digit branch code)
         */
        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "IFSC code must be in valid format (e.g., HDFC0001234)")
        String ifscCode,

        /**
         * Optional: Account number for bank transfers
         */
        @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be 9-18 digits")
        String accountNumber
) {
    /**
     * Returns masked beneficiary account for logging
     */
    public String getMaskedAccount() {
        if (beneficiaryAccount == null || beneficiaryAccount.length() < 4) {
            return "****";
        }
        
        // If it's a UPI ID, mask it
        if (beneficiaryAccount.contains("@")) {
            int atIndex = beneficiaryAccount.indexOf('@');
            String username = beneficiaryAccount.substring(0, Math.min(atIndex, 2));
            String domain = beneficiaryAccount.substring(atIndex + 1);
            return username + "***@" + domain;
        }
        
        // For account numbers, show last 4 digits
        return "****" + beneficiaryAccount.substring(beneficiaryAccount.length() - 4);
    }
}
