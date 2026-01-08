package com.sushilk.payment_service.exceptions;

public class PaymentAlreadyExistsException extends RuntimeException{
    public PaymentAlreadyExistsException(String message) {
        super(message);
    }
}
