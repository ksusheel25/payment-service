package com.sushilk.payment_service.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to validate UPI ID (VPA - Virtual Payment Address)
 * Valid formats:
 * - username@bankname (e.g., user@paytm, user@ybl, user@okaxis)
 * - phone@provider (e.g., 9876543210@paytm, 9876543210@ybl)
 * - user@upi (e.g., user@upi)
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidUPIId.UPIIdValidator.class)
@Documented
public @interface ValidUPIId {
    String message() default "Invalid UPI ID format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class UPIIdValidator implements ConstraintValidator<ValidUPIId, String> {
        private static final String UPI_ID_PATTERN = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+$";
        
        // Common UPI handle identifiers (non-exhaustive list)
        private static final String[] VALID_UPI_HANDLES = {
            "paytm", "ybl", "okaxis", "okhdfcbank", "okicici", "oksbi", 
            "okaxis", "payu", "airtel", "phonepe", "gpay", "amazonpay",
            "upi", "axl", "ibl", "yesbank", "kvb", "paytm", "payzapp",
            "rbl", "sbi", "unionbank", "upi", "waayu"
        };

        @Override
        public void initialize(ValidUPIId constraintAnnotation) {
            // No initialization needed
        }

        @Override
        public boolean isValid(String upiId, ConstraintValidatorContext context) {
            if (upiId == null || upiId.isBlank()) {
                return true; // Let @NotBlank handle null/empty validation
            }

            // Basic format check: must contain @ symbol and valid characters
            if (!upiId.matches(UPI_ID_PATTERN)) {
                return false;
            }

            // Must contain exactly one @
            int atCount = upiId.length() - upiId.replace("@", "").length();
            if (atCount != 1) {
                return false;
            }

            String[] parts = upiId.split("@");
            if (parts.length != 2) {
                return false;
            }

            String username = parts[0];
            String handle = parts[1].toLowerCase();

            // Username validation
            if (username.length() < 1 || username.length() > 255) {
                return false;
            }

            // Handle validation - must be a valid UPI handle
            // Check if handle is a known UPI provider/handle
            // Note: This is a basic check. In production, you might want to check against a comprehensive list
            boolean isValidHandle = false;
            for (String validHandle : VALID_UPI_HANDLES) {
                if (handle.equals(validHandle) || handle.endsWith("." + validHandle)) {
                    isValidHandle = true;
                    break;
                }
            }

            // Also allow generic patterns like xyz@bankname or xyz@upi
            if (!isValidHandle) {
                // Allow if handle looks reasonable (contains letters/numbers and dots/hyphens)
                isValidHandle = handle.matches("^[a-zA-Z0-9.-]+$") && handle.length() >= 2 && handle.length() <= 63;
            }

            return isValidHandle;
        }
    }
}
