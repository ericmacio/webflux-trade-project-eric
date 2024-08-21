package com.eric.aggregator.dto;

import com.eric.aggregator.domain.Ticker;
import com.eric.aggregator.domain.TradeAction;

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
