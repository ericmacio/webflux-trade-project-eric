package com.eric.customerportfolio.mapper;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.dto.CustomerInformation;
import com.eric.customerportfolio.dto.Holding;
import com.eric.customerportfolio.dto.StockTradeRequest;
import com.eric.customerportfolio.dto.StockTradeResponse;
import com.eric.customerportfolio.entity.Customer;
import com.eric.customerportfolio.entity.PortfolioItem;

import java.util.List;

public class EntityDtoMapper {

    public static CustomerInformation toCustomerInformation(Customer customer, List<PortfolioItem> items) {
        List<Holding> holdings = items.stream()
                .map(item -> new Holding(item.getTicker(), item.getQuantity()))
                .toList();
        return new CustomerInformation(customer.getId(), customer.getName(), customer.getBalance(), holdings);
    }

    public static StockTradeResponse toStockTradeResponse(Customer customer, StockTradeRequest tradeRequest) {
        return new StockTradeResponse(customer.getId(), tradeRequest.quantity(),
                tradeRequest.ticker(), tradeRequest.price(), tradeRequest.action(),
                tradeRequest.getTotalPrice(), customer.getBalance());
    }

    public static PortfolioItem toPortFolioItem(Integer customerId, Ticker ticker) {
        return new PortfolioItem(null, customerId, ticker, 0);
    }
}