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

import com.sushilk.payment_service.dtos.InitiatePaymentRequest;
import com.sushilk.payment_service.enums.PaymentMethod;

/**
 * Custom validation to ensure card details are provided when payment method is CARD
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardDetailsRequired.CardDetailsRequiredValidator.class)
@Documented
public @interface CardDetailsRequired {
    String message() default "Card details are required when payment method is CARD";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class CardDetailsRequiredValidator implements ConstraintValidator<CardDetailsRequired, InitiatePaymentRequest> {
        @Override
        public void initialize(CardDetailsRequired constraintAnnotation) {
            // No initialization needed
        }

        @Override
        public boolean isValid(InitiatePaymentRequest request, ConstraintValidatorContext context) {
            if (request == null) {
                return true; // Let other validations handle null
            }

            // If payment method is CARD, card details must be provided
            if (request.paymentMethod() == PaymentMethod.CARD) {
                if (request.cardDetails() == null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Card details are required when payment method is CARD"
                    ).addPropertyNode("cardDetails").addConstraintViolation();
                    return false;
                }
                
            }

            // If payment method is not CARD, card details should not be provided
            if (request.paymentMethod() != PaymentMethod.CARD && request.cardDetails() != null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Card details should not be provided when payment method is not CARD"
                ).addPropertyNode("cardDetails").addConstraintViolation();
                return false;
            }

            return true;
        }
    }
}
