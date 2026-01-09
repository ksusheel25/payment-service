package com.sushilk.payment_service.dtos;

import com.sushilk.payment_service.validation.ValidCardExpiry;
import com.sushilk.payment_service.validation.ValidCardNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Card details DTO for card payments.
 * Note: For PCI compliance, card details should be tokenized or passed securely to payment gateway.
 * This DTO should not be stored in database in raw form.
 */
public record CardDetails(
        @NotBlank(message = "Card number is required for card payments")
        @Size(min = 13, max = 19, message = "Card number must be between 13 and 19 digits")
        @Pattern(regexp = "^[0-9]+$", message = "Card number must contain only digits")
        @ValidCardNumber(message = "Card number is invalid (failed Luhn algorithm check)")
        String cardNumber,

        @NotBlank(message = "Cardholder name is required")
        @Size(min = 2, max = 100, message = "Cardholder name must be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Cardholder name must contain only letters and spaces")
        String cardholderName,

        @NotBlank(message = "Expiry date is required (format: MM/YY)")
        @Pattern(regexp = "^(0[1-9]|1[0-2])/([0-9]{2})$", message = "Expiry date must be in MM/YY format (e.g., 12/25)")
        @ValidCardExpiry(message = "Card expiry date is invalid or has expired. Please use a valid future date")
        String expiryDate,

        @NotBlank(message = "CVV is required")
        @Size(min = 3, max = 4, message = "CVV must be 3 or 4 digits")
        @Pattern(regexp = "^[0-9]+$", message = "CVV must contain only digits")
        String cvv
) {
    /**
     * Returns masked card number for logging (only last 4 digits visible)
     * Use this method instead of cardNumber for logging to maintain PCI compliance
     */
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleaned = cardNumber.replaceAll("[\\s-]", "");
        return "****-****-****-" + cleaned.substring(cleaned.length() - 4);
    }
}
