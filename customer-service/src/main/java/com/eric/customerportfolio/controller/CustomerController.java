package com.eric.customerportfolio.controller;

import com.eric.customerportfolio.dto.CustomerInformation;
import com.eric.customerportfolio.dto.StockTradeRequest;
import com.eric.customerportfolio.dto.StockTradeResponse;
import com.eric.customerportfolio.service.CustomerService;
import com.eric.customerportfolio.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final TradeService tradeService;

    public CustomerController(CustomerService customerService, TradeService tradeService) {
        this.customerService = customerService;
        this.tradeService = tradeService;
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerInformation> getCustomerInformation(@PathVariable Integer customerId) {
        return customerService.getCustomerInformation(customerId);
    }

    @PostMapping("/{customerId}/trade")
    public Mono<StockTradeResponse> postTradeRequest(@PathVariable Integer customerId,
                                                     @RequestBody Mono<StockTradeRequest> tradeRequestMono) {
        return tradeRequestMono
                .flatMap(request -> tradeService.tradeRequest(customerId, request));
    }
}
