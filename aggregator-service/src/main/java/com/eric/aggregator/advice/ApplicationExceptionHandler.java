package com.eric.aggregator.advice;

import com.eric.aggregator.exceptions.CustomerNotFoundException;
import com.eric.aggregator.exceptions.InvalidTradeRequestException;
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
                "Customer Not Found",
                "http://example.com/errors/customer-not-found");
    }

    @ExceptionHandler(InvalidTradeRequestException.class)
    public ProblemDetail handleException(InvalidTradeRequestException ex) {
        return buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                "Invalid Trade Request",
                "http://example.com/errors/invalid-trade-request");
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String msg, String title, String url) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, msg);
        problemDetail.setType(URI.create(url));
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
