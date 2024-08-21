package com.eric.customerportfolio.service;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.dto.StockTradeRequest;
import com.eric.customerportfolio.dto.StockTradeResponse;
import com.eric.customerportfolio.entity.Customer;
import com.eric.customerportfolio.entity.PortfolioItem;
import com.eric.customerportfolio.exceptions.ApplicationExceptions;
import com.eric.customerportfolio.mapper.EntityDtoMapper;
import com.eric.customerportfolio.repository.CustomerRepository;
import com.eric.customerportfolio.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class TradeService {

    private final CustomerRepository customerRepository;
    private final PortfolioRepository portfolioRepository;

    public TradeService(CustomerRepository customerRepository, PortfolioRepository portfolioRepository) {
        this.customerRepository = customerRepository;
        this.portfolioRepository = portfolioRepository;
    }

    @Transactional
    public Mono<StockTradeResponse> tradeRequest(Integer customerId, StockTradeRequest tradeRequest) {
        return switch(tradeRequest.action()) {
            case BUY -> this.buyRequest(customerId, tradeRequest);
            case SELL -> this.sellRequest(customerId, tradeRequest);
        };
    }

    private Mono<StockTradeResponse> buyRequest(Integer customerId, StockTradeRequest tradeBuyRequest) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .filter(customer -> customer.getBalance() > tradeBuyRequest.getTotalPrice())
                .switchIfEmpty(ApplicationExceptions.insufficientBalance(customerId))
                .flatMap(customer -> buildStockTradeBuyResponse(customer, tradeBuyRequest));
    }

    private Mono<StockTradeResponse> sellRequest(Integer customerId, StockTradeRequest tradeBuyRequest) {
        return customerRepository.findById(customerId)
                .switchIfEmpty(ApplicationExceptions.customerNotFound(customerId))
                .flatMap(customer -> buildStockTradeSellResponse(customer, tradeBuyRequest));
    }

    private Mono<StockTradeResponse> buildStockTradeBuyResponse(Customer customer, StockTradeRequest buyRequest) {
        Integer newBalance = customer.getBalance() - buyRequest.getTotalPrice();
        return Mono.zip(
                        updateCustomer(customer, newBalance),
                        creditPortfolioItem(customer.getId(), buyRequest.ticker(), buyRequest.quantity())
                )
                .map(t -> EntityDtoMapper.toStockTradeResponse(t.getT1(), buyRequest));
    }

    private Mono<StockTradeResponse> buildStockTradeSellResponse(Customer customer, StockTradeRequest sellRequest) {
        Integer newBalance = customer.getBalance() + sellRequest.getTotalPrice();
        return Mono.zip(
                    updateCustomer(customer, newBalance),
                    debitPortfolioItem(customer.getId(), sellRequest.ticker(), sellRequest.quantity())
                )
                .map(t -> EntityDtoMapper.toStockTradeResponse(t.getT1(), sellRequest));
    }

    private Mono<PortfolioItem> creditPortfolioItem(Integer customerId, Ticker ticker, Integer quantity) {
        return this.getCurrentPortfolioItem(customerId, ticker)
                .flatMap(item -> updatePortfolioItem(item, item.getQuantity() + quantity));
    }

    private Mono<PortfolioItem> debitPortfolioItem(Integer customerId, Ticker ticker, Integer quantity) {
        return this.getCurrentPortfolioItem(customerId, ticker)
                .filter(item -> item.getQuantity() >= quantity)
                .switchIfEmpty(ApplicationExceptions.insufficientShares(customerId))
                .flatMap(item -> updatePortfolioItem(item, item.getQuantity() - quantity));
    }

    private Mono<PortfolioItem> getCurrentPortfolioItem(Integer customerId, Ticker ticker) {
        return this.portfolioRepository.findByCustomerIdAndTicker(customerId, ticker)
                .defaultIfEmpty(EntityDtoMapper.toPortFolioItem(customerId, ticker));
    }

    private Mono<PortfolioItem> updatePortfolioItem(PortfolioItem item, Integer quantity) {
        return portfolioRepository.save(
                new PortfolioItem(item.getId(), item.getCustomerId(), item.getTicker(), quantity));
    }

    private Mono<Customer> updateCustomer(Customer customer, Integer newBalance) {
        return this.customerRepository.save(
                new Customer(customer.getId(), customer.getName(), newBalance));
    }

}
