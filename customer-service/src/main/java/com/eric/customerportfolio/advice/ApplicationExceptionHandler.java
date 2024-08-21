package com.eric.customerportfolio.advice;

import com.eric.customerportfolio.exceptions.CustomerNotFoundException;
import com.eric.customerportfolio.exceptions.InsufficientBalanceException;
import com.eric.customerportfolio.exceptions.InsufficientSharesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleException(CustomerNotFoundException ex) {
        return buildProblemDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                "Customer Not Found", "http://example.com/errors/customer-not-found");
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ProblemDetail handleException(InsufficientBalanceException ex) {
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Insufficient Balance", "http://example.com/errors/insufficient-balance");
    }

    @ExceptionHandler(InsufficientSharesException.class)
    public ProblemDetail handleException(InsufficientSharesException ex) {
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Insufficient Shares", "http://example.com/errors/insufficient-shares");
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String msg, String title, String url) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, msg);
        problemDetail.setType(URI.create(url));
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
