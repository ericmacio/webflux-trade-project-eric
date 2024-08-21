package com.eric.customerportfolio.exceptions;

public class CustomerNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Customer [id=%d] not found";

    public CustomerNotFoundException(Integer customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
