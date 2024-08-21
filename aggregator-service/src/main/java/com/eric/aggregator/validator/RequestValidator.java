package com.eric.aggregator.validator;

import com.eric.aggregator.dto.TradeRequest;
import com.eric.aggregator.exceptions.ApplicationExceptions;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class RequestValidator {

    public static UnaryOperator<Mono<TradeRequest>> validate() {
        return mono -> mono
                .filter(hasTicker())
                .switchIfEmpty(ApplicationExceptions.missingTicker())
                .filter(tradeActionIsPresent())
                .switchIfEmpty(ApplicationExceptions.missingTradeAction())
                .filter(quantityIsValid())
                .switchIfEmpty(ApplicationExceptions.invalidQuantity());
    }

    private static Predicate<TradeRequest> hasTicker() {
        return dto -> Objects.nonNull(dto.ticker());
    }

    private static Predicate<TradeRequest> tradeActionIsPresent() {
        return dto -> Objects.nonNull(dto.action());
    }

    private static Predicate<TradeRequest> quantityIsValid() {
        return dto -> Objects.nonNull(dto.quantity()) && dto.quantity() > 0;
    }
}
