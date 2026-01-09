package com.sushilk.payment_service.dtos;

import com.sushilk.payment_service.validation.ValidUPIId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * UPI payment details DTO.
 * UPI ID (VPA - Virtual Payment Address) is required for UPI payments.
 * Format: username@bankname or phone@provider (e.g., user@paytm, 9876543210@ybl, john@gpay)
 */
public record UPIDetails(
        @NotBlank(message = "UPI ID (VPA) is required for UPI payments")
        @Size(min = 5, max = 256, message = "UPI ID must be between 5 and 256 characters")
        @ValidUPIId(message = "Invalid UPI ID format. Valid format: username@bankname (e.g., user@paytm, user@ybl, user@gpay)")
        String upiId,

        /**
         * Optional phone number for UPI payments (some providers may require it)
         * Format: 10-digit Indian mobile number
         */
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number starting with 6-9")
        String phoneNumber
) {
    /**
     * Returns masked UPI ID for logging (shows only first part and last part)
     * Example: user@paytm -> user@***tm
     */
    public String getMaskedUPIId() {
        if (upiId == null || upiId.length() < 3) {
            return "***@***";
        }
        
        int atIndex = upiId.indexOf('@');
        if (atIndex <= 0 || atIndex >= upiId.length() - 1) {
            return "***@***";
        }
        
        String username = upiId.substring(0, atIndex);
        String domain = upiId.substring(atIndex + 1);
        
        // Mask domain but show first and last 2 characters if domain is long enough
        if (domain.length() <= 4) {
            return username + "@****";
        }
        return username + "@" + domain.substring(0, 2) + "***" + domain.substring(domain.length() - 2);
    }
    
    /**
     * Returns masked phone number for logging (shows only last 4 digits)
     */
    public String getMaskedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return "******";
        }
        return "******" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
