package com.eric.customerportfolio.dto;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.domain.TradeAction;

public record StockTradeResponse(
        Integer customerId,
        Integer quantity,
        Ticker ticker,
        Integer price,
        TradeAction action,
        Integer totalPrice,
        Integer balance
) {
}
