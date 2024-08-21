package com.eric.customerportfolio.dto;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.domain.TradeAction;

public record StockTradeRequest(
        Ticker ticker,
        Integer price,
        Integer quantity,
        TradeAction action
) {

    public Integer getTotalPrice() {
        return this.price * this.quantity;
    }
}
