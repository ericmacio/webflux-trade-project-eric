package com.eric.customerportfolio.exceptions;

public class InsufficientSharesException extends RuntimeException {

    private static final String MESSAGE = "Insufficient shares for customer [id=%d] to complete the transaction";

    public InsufficientSharesException(Integer customerId) {
        super(MESSAGE.formatted(customerId));
    }
}
