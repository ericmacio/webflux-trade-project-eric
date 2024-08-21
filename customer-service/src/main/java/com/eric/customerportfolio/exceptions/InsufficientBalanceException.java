package com.eric.customerportfolio.exceptions;

public class InsufficientBalanceException extends RuntimeException {

    private static final String MESSAGE = "Insufficient balance for customer [id=%d] to complete the transaction";

    public InsufficientBalanceException(Integer customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
