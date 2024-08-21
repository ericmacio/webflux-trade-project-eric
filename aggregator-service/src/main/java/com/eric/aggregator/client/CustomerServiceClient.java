package com.eric.aggregator.client;

import com.eric.aggregator.dto.CustomerInformation;
import com.eric.aggregator.dto.StockTradeRequest;
import com.eric.aggregator.dto.StockTradeResponse;
import com.eric.aggregator.dto.TradeRequest;
import com.eric.aggregator.exceptions.ApplicationExceptions;
import com.eric.aggregator.exceptions.CustomerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Objects;

public class CustomerServiceClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceClient.class);

    private final WebClient client;
    private StockServiceClient stockServiceClient;

    public CustomerServiceClient(WebClient client) {
        this.client = client;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        log.info("GET request /customers/{}", customerId);
        return client.get()
                .uri("/customers/{customerId}", customerId)
                .retrieve()
                .bodyToMono(CustomerInformation.class)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> ApplicationExceptions.customerNotFound(customerId));
    }

    public Mono<StockTradeResponse> postStockTradeRequest(Integer customerId, StockTradeRequest stockTradeRequest) {
        return client.post()
                .uri("/customers/{customerId}/trade", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(stockTradeRequest)
                .retrieve()
                .bodyToMono(StockTradeResponse.class)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> ApplicationExceptions.customerNotFound(customerId))
                .onErrorResume(WebClientResponseException.BadRequest.class, this::handleException);
    }

    private <T> Mono<T> handleException(WebClientResponseException.BadRequest exception) {
        ProblemDetail problemDetail = exception.getResponseBodyAs(ProblemDetail.class);
        String message = Objects.nonNull(problemDetail) ? problemDetail.getDetail() : exception.getMessage();
        log.error("customer service error: {}", problemDetail);
        return ApplicationExceptions.invalidTradeRequest(message);
    }

}
