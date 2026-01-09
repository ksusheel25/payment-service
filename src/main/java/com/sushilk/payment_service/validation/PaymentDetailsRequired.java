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
import com.sushilk.payment_service.enums.OrderType;
import com.sushilk.payment_service.enums.PaymentMethod;
import com.sushilk.payment_service.enums.PaymentProvider;

/**
 * Custom validation to ensure appropriate payment details are provided based on PROVIDER:
 * - CARD provider: Card details required, paymentMethod must be CARD
 * - PHONEPE/PAYTM/GOOGLEPAY provider: UPI details required, paymentMethod must be UPI
 * - NET_BANKING paymentMethod: Net banking details required (future provider implementation)
 * 
 * Also validates beneficiary details based on order type:
 * - P2P, BILL_PAYMENT, DONATION: Beneficiary details required
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PaymentDetailsRequired.PaymentDetailsRequiredValidator.class)
@Documented
public @interface PaymentDetailsRequired {
    String message() default "Payment details are required based on payment method";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class PaymentDetailsRequiredValidator implements ConstraintValidator<PaymentDetailsRequired, InitiatePaymentRequest> {
        @Override
        public void initialize(PaymentDetailsRequired constraintAnnotation) {
            // No initialization needed
        }

        @Override
        public boolean isValid(InitiatePaymentRequest request, ConstraintValidatorContext context) {
            if (request == null) {
                return true; // Let other validations handle null
            }

            PaymentProvider provider = request.provider();
            PaymentMethod paymentMethod = request.paymentMethod();
            OrderType orderType = request.orderType();
            
            // 1️⃣ Validate provider and payment method compatibility
            
            // CARD provider must use CARD payment method
            if (provider == PaymentProvider.CARD) {
                if (paymentMethod != PaymentMethod.CARD) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Payment method must be CARD when provider is CARD"
                    ).addPropertyNode("paymentMethod").addConstraintViolation();
                    return false;
                }
            }
            
            // UPI providers (PHONEPE, PAYTM, GOOGLEPAY) must use UPI payment method
            if (provider == PaymentProvider.PHONEPE || provider == PaymentProvider.PAYTM || provider == PaymentProvider.GOOGLEPAY) {
                if (paymentMethod != PaymentMethod.UPI) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("Payment method must be UPI when provider is %s", provider)
                    ).addPropertyNode("paymentMethod").addConstraintViolation();
                    return false;
                }
            }

            // 2️⃣ Validate payment details based on PROVIDER (primary validation)
            
            // CARD provider requires card details
            if (provider == PaymentProvider.CARD) {
                if (request.cardDetails() == null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Card details are required when provider is CARD"
                    ).addPropertyNode("cardDetails").addConstraintViolation();
                    return false;
                }
                // Reject other payment details
                if (request.upiDetails() != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "UPI details should not be provided when provider is CARD"
                    ).addPropertyNode("upiDetails").addConstraintViolation();
                    return false;
                }
                if (request.netBankingDetails() != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Net banking details should not be provided when provider is CARD"
                    ).addPropertyNode("netBankingDetails").addConstraintViolation();
                    return false;
                }
            }

            // UPI providers (PHONEPE, PAYTM, GOOGLEPAY) require UPI details
            if (provider == PaymentProvider.PHONEPE || provider == PaymentProvider.PAYTM || provider == PaymentProvider.GOOGLEPAY) {
                if (request.upiDetails() == null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("UPI details are required when provider is %s", provider)
                    ).addPropertyNode("upiDetails").addConstraintViolation();
                    return false;
                }
                // Reject other payment details
                if (request.cardDetails() != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("Card details should not be provided when provider is %s", provider)
                    ).addPropertyNode("cardDetails").addConstraintViolation();
                    return false;
                }
                if (request.netBankingDetails() != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("Net banking details should not be provided when provider is %s", provider)
                    ).addPropertyNode("netBankingDetails").addConstraintViolation();
                    return false;
                }
            }

            // 3️⃣ Additional validation: Payment method should also be consistent
            // (This is a secondary check to ensure data integrity)
            
            if (paymentMethod == PaymentMethod.CARD && request.cardDetails() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "Card details are required when payment method is CARD"
                ).addPropertyNode("cardDetails").addConstraintViolation();
                return false;
            }

            if (paymentMethod == PaymentMethod.UPI && request.upiDetails() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "UPI details are required when payment method is UPI"
                ).addPropertyNode("upiDetails").addConstraintViolation();
                return false;
            }

            if (paymentMethod == PaymentMethod.NET_BANKING) {
                if (request.netBankingDetails() == null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Net banking details are required when payment method is NET_BANKING"
                    ).addPropertyNode("netBankingDetails").addConstraintViolation();
                    return false;
                }
                // NET_BANKING is not yet associated with any provider, so we validate only payment method
                if (request.cardDetails() != null || request.upiDetails() != null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            "Card/UPI details should not be provided when payment method is NET_BANKING"
                    ).addPropertyNode("paymentMethod").addConstraintViolation();
                    return false;
                }
            }

            // 2️⃣ Validate beneficiary details based on order type
            
            // P2P, BILL_PAYMENT, DONATION require beneficiary details
            if (orderType == OrderType.P2P || orderType == OrderType.BILL_PAYMENT || orderType == OrderType.DONATION) {
                if (request.beneficiaryDetails() == null) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                            String.format("Beneficiary details are required when order type is %s", orderType)
                    ).addPropertyNode("beneficiaryDetails").addConstraintViolation();
                    return false;
                }
            }

            // WALLET top-up to another wallet also requires beneficiary
            if (orderType == OrderType.WALLET && request.beneficiaryDetails() == null) {
                // For wallet-to-wallet transfer, beneficiary is required
                // For self top-up, beneficiary can be same as userId (optional check can be added)
            }

            return true;
        }
    }
}
