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
 * Custom validation annotation to validate card number using Luhn algorithm
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCardNumber.CardNumberValidator.class)
@Documented
public @interface ValidCardNumber {
    String message() default "Invalid card number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class CardNumberValidator implements ConstraintValidator<ValidCardNumber, String> {
        @Override
        public void initialize(ValidCardNumber constraintAnnotation) {
            // No initialization needed
        }

        @Override
        public boolean isValid(String cardNumber, ConstraintValidatorContext context) {
            if (cardNumber == null || cardNumber.isBlank()) {
                return true; // Let @NotBlank handle null/empty validation
            }

            // Remove any spaces or hyphens
            String cleaned = cardNumber.replaceAll("[\\s-]", "");

            // Check if all characters are digits
            if (!cleaned.matches("^[0-9]+$")) {
                return false;
            }

            // Check length (13-19 digits)
            if (cleaned.length() < 13 || cleaned.length() > 19) {
                return false;
            }

            // Luhn algorithm validation
            return isValidLuhn(cleaned);
        }

        /**
         * Luhn algorithm (Mod 10) implementation
         * 1. Starting from the rightmost digit, double every second digit
         * 2. If doubling results in a two-digit number, add the two digits
         * 3. Sum all digits
         * 4. If the sum is divisible by 10, the card number is valid
         */
        private boolean isValidLuhn(String cardNumber) {
            int sum = 0;
            boolean alternate = false;

            // Process from right to left
            for (int i = cardNumber.length() - 1; i >= 0; i--) {
                int digit = Character.getNumericValue(cardNumber.charAt(i));

                if (alternate) {
                    digit *= 2;
                    if (digit > 9) {
                        digit = (digit % 10) + 1; // Add digits of two-digit number
                    }
                }

                sum += digit;
                alternate = !alternate;
            }

            return sum % 10 == 0;
        }
    }
}
