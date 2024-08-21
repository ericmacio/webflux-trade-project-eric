package com.eric.aggregator.controller;

import com.eric.aggregator.client.CustomerServiceClient;
import com.eric.aggregator.dto.CustomerInformation;
import com.eric.aggregator.dto.StockTradeResponse;
import com.eric.aggregator.dto.TradeRequest;
import com.eric.aggregator.service.CustomerPortfolioService;
import com.eric.aggregator.validator.RequestValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerPortfolioController {

    private static final Logger log = LoggerFactory.getLogger(CustomerPortfolioController.class);
    private final CustomerPortfolioService customerPortfolioService;

    public CustomerPortfolioController(CustomerPortfolioService customerPortfolioService) {
        this.customerPortfolioService = customerPortfolioService;
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerInformation> getCustomerInformation(@PathVariable Integer customerId) {
        log.info("Get customer information for customer id: {}", customerId);
        return this.customerPortfolioService.getCustomerInformation(customerId);
    }

    @PostMapping("/{customerId}/trade")
    public Mono<StockTradeResponse> postTradeRequest(@PathVariable Integer customerId, @RequestBody Mono<TradeRequest> tradeRequest) {
        return tradeRequest
                .transform(RequestValidator.validate())
                .flatMap(req -> this.customerPortfolioService.sendTradeRequest(customerId, req));
    }
}
