package com.eric.customerportfolio;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.domain.TradeAction;
import com.eric.customerportfolio.dto.StockTradeRequest;
import com.eric.customerportfolio.dto.StockTradeResponse;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomerServiceIntegrationTests {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceIntegrationTests.class);

    @Autowired
    private WebTestClient client;

    @Test
    public void CustomerInformation() {
        this.getCustomer(1,HttpStatus.OK)
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Sam")
                .jsonPath("$.balance").isEqualTo(10000)
                .jsonPath("$.holdings").isEmpty();
    }

    @Test
    public void buyAndSell() {

        Integer customerId = 1;
        Integer price = 10;
        Integer buyQty = 15;
        Ticker ticker = Ticker.AMAZON;
        int initBalance = 10000;
        int expectedBalance = initBalance - (price * buyQty);

        StockTradeRequest buyRequest = new StockTradeRequest(ticker, price, buyQty, TradeAction.BUY);
        postTradeRequest(customerId, HttpStatus.OK, buyRequest)
                .jsonPath("$.customerId").isEqualTo(customerId)
                .jsonPath("$.action").isEqualTo(TradeAction.BUY.toString())
                .jsonPath("$.quantity").isEqualTo(buyQty)
                .jsonPath("$.ticker").isEqualTo(ticker.toString())
                .jsonPath("$.price").isEqualTo(price)
                .jsonPath("$.totalPrice").isEqualTo(price * buyQty)
                .jsonPath("$.balance").isEqualTo(expectedBalance);

        this.getCustomer(customerId,HttpStatus.OK)
                .jsonPath("$.id").isEqualTo(customerId)
                .jsonPath("$.balance").isEqualTo(expectedBalance)
                .jsonPath("$.holdings").isArray()
                .jsonPath("$.holdings[0].ticker").isEqualTo(ticker.toString())
                .jsonPath("$.holdings[0].quantity").isEqualTo(buyQty);

        Integer sellQty = 10;
        expectedBalance += (price * sellQty);
        StockTradeRequest sellRequest = new StockTradeRequest(ticker, price, sellQty, TradeAction.SELL);
        postTradeRequest(customerId, HttpStatus.OK, sellRequest)
                .jsonPath("$.customerId").isEqualTo(customerId)
                .jsonPath("$.action").isEqualTo(TradeAction.SELL.toString())
                .jsonPath("$.quantity").isEqualTo(sellQty)
                .jsonPath("$.ticker").isEqualTo(ticker.toString())
                .jsonPath("$.price").isEqualTo(price)
                .jsonPath("$.totalPrice").isEqualTo(price * sellQty)
                .jsonPath("$.balance").isEqualTo(expectedBalance);

        this.getCustomer(customerId,HttpStatus.OK)
                .jsonPath("$.id").isEqualTo(customerId)
                .jsonPath("$.balance").isEqualTo(expectedBalance)
                .jsonPath("$.holdings").isArray()
                .jsonPath("$.holdings[0].ticker").isEqualTo(ticker.toString())
                .jsonPath("$.holdings[0].quantity").isEqualTo(buyQty - sellQty);
    }

    @Test
    public void customerNotFound() {
        this.getCustomer(10,HttpStatus.NOT_FOUND)
                .jsonPath("$.title").isEqualTo("Customer Not Found")
                .jsonPath("$.detail").isEqualTo("Customer [id=10] not found");
    }

    @Test
    public void insufficientBalance() {
        Integer customerId = 1;
        Integer price = 100;
        Integer qty = 50000;
        Ticker ticker = Ticker.AMAZON;

        StockTradeRequest buyRequest = new StockTradeRequest(ticker, price, qty, TradeAction.BUY);
        postTradeRequest(customerId, HttpStatus.BAD_REQUEST, buyRequest)
                .jsonPath("$.title").isEqualTo("Insufficient Balance")
                .jsonPath("$.detail").isEqualTo("Insufficient balance for customer [id=1] to complete the transaction");
    }

    @Test
    public void insufficientShares() {
        Integer customerId = 1;
        Integer price = 100;
        Integer qty = 50000;
        Ticker ticker = Ticker.AMAZON;

        StockTradeRequest buyRequest = new StockTradeRequest(ticker, price, qty, TradeAction.SELL);
        postTradeRequest(customerId, HttpStatus.BAD_REQUEST, buyRequest)
                .jsonPath("$.title").isEqualTo("Insufficient Shares")
                .jsonPath("$.detail").isEqualTo("Insufficient shares for customer [id=1] to complete the transaction");
    }

    private WebTestClient.BodyContentSpec getCustomer(Integer customerId, HttpStatus expectedStatus) {
        return client.get()
                .uri("/customers/{id}", customerId)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

    private WebTestClient.BodyContentSpec postTradeRequest(Integer customerId, HttpStatus expectedStatus, StockTradeRequest req) {
        return client.post()
                .uri("/customers/{id}/trade", customerId)
                .bodyValue(req)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

}
