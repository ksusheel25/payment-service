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
import java.time.YearMonth;

/**
 * Custom validation annotation to validate that card expiry date is not in the past
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidCardExpiry.CardExpiryValidator.class)
@Documented
public @interface ValidCardExpiry {
    String message() default "Card expiry date is invalid or has expired";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class CardExpiryValidator implements ConstraintValidator<ValidCardExpiry, String> {
        @Override
        public void initialize(ValidCardExpiry constraintAnnotation) {
            // No initialization needed
        }

        @Override
        public boolean isValid(String expiryDate, ConstraintValidatorContext context) {
            if (expiryDate == null || expiryDate.isBlank()) {
                return true; // Let @NotBlank handle null/empty validation
            }

            // Format should already be validated by @Pattern, but double-check
            if (!expiryDate.matches("^(0[1-9]|1[0-2])/([0-9]{2})$")) {
                return false;
            }

            try {
                String[] parts = expiryDate.split("/");
                int month = Integer.parseInt(parts[0]);
                int year = Integer.parseInt("20" + parts[1]); // Convert YY to YYYY (e.g., 25 -> 2025)

                YearMonth expiry = YearMonth.of(year, month);
                YearMonth current = YearMonth.now();

                // Expiry date should not be in the past
                // Allow current month as cards are typically valid until end of month
                return !expiry.isBefore(current);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
