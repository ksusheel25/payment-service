package com.sushilk.payment_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Net banking payment details.
 * Required for NET_BANKING payment method.
 * Contains bank selection and authentication details.
 */
public record NetBankingDetails(
        /**
         * Bank code/identifier (e.g., HDFC, ICICI, SBI)
         * This identifies which bank's net banking portal to redirect to
         */
        @NotBlank(message = "Bank code is required for net banking payments")
        @Size(min = 2, max = 50, message = "Bank code must be between 2 and 50 characters")
        String bankCode,

        /**
         * Bank name for display purposes (e.g., "HDFC Bank", "ICICI Bank")
         */
        @Size(max = 100, message = "Bank name must not exceed 100 characters")
        String bankName,

        /**
         * Customer ID or User ID for the bank net banking portal
         * Optional - some banks may require this, others may redirect to login
         */
        @Size(max = 100, message = "Customer ID must not exceed 100 characters")
        String customerId
) {
    /**
     * Returns masked customer ID for logging
     */
    public String getMaskedCustomerId() {
        if (customerId == null || customerId.length() < 4) {
            return "****";
        }
        return "****" + customerId.substring(customerId.length() - 4);
    }
}
