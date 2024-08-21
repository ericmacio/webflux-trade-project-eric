package com.eric.customerportfolio.entity;

import com.eric.customerportfolio.domain.Ticker;
import org.springframework.data.annotation.Id;

public class PortfolioItem {

    @Id
    private final Integer id;
    private final Integer customerId;
    private final Ticker ticker;
    private final Integer quantity;

    public PortfolioItem(Integer id, Integer customerId, Ticker ticker, Integer quantity) {
        this.id = id;
        this.customerId = customerId;
        this.ticker = ticker;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public Integer getQuantity() {
        return quantity;
    }

}
