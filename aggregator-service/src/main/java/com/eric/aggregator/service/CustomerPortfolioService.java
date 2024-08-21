package com.eric.aggregator.service;

import com.eric.aggregator.client.CustomerServiceClient;
import com.eric.aggregator.client.StockServiceClient;
import com.eric.aggregator.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CustomerPortfolioService {

    private final CustomerServiceClient customerServiceClient;
    private final StockServiceClient stockServiceClient;

    public CustomerPortfolioService(CustomerServiceClient customerServiceClient, StockServiceClient stockServiceClient) {
        this.customerServiceClient = customerServiceClient;
        this.stockServiceClient = stockServiceClient;
    }

    public Mono<CustomerInformation> getCustomerInformation(Integer customerId) {
        return this.customerServiceClient.getCustomerInformation(customerId);
    }

    public Mono<StockTradeResponse> sendTradeRequest(Integer customerId, TradeRequest tradeRequest) {
        return this.stockServiceClient.getStockPrice(tradeRequest.ticker())
                .map(StockPriceResponse::price)
                .map(price -> getStockTradeRequest(tradeRequest, price))
                .flatMap(stockTradeRequest -> sendStockTradeRequest(customerId, stockTradeRequest));
    }

    private static StockTradeRequest getStockTradeRequest(TradeRequest tradeRequest, Integer price) {
        return new StockTradeRequest(tradeRequest.ticker(), price, tradeRequest.quantity(), tradeRequest.action());
    }

    private Mono<StockTradeResponse> sendStockTradeRequest(Integer customerId, StockTradeRequest stockTradeRequest) {
        return this.customerServiceClient.postStockTradeRequest(customerId, stockTradeRequest);
    }


}
