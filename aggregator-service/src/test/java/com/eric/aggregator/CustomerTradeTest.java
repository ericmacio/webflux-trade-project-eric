package com.eric.aggregator;

import com.eric.aggregator.domain.Ticker;
import com.eric.aggregator.domain.TradeAction;
import com.eric.aggregator.dto.TradeRequest;
import org.junit.jupiter.api.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.MediaType;
import org.mockserver.model.RegexBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

public class CustomerTradeTest extends AbstractIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(CustomerTradeTest.class);

    @Test
    public void tradeSuccess() {

        // mock customer service
        mockCustomerTrade("customer-service/customer-trade-200.json", 200);

        TradeRequest tradeRequest = new TradeRequest(Ticker.GOOGLE, TradeAction.BUY, 2);
        postTradeRequest(tradeRequest, HttpStatus.OK)
                .jsonPath("$.balance").isEqualTo(9780)
                .jsonPath("$.totalPrice").isEqualTo(220);

    }

    @Test
    public void tradeFailure() {

        // mock customer service
        mockCustomerTrade("customer-service/customer-trade-400.json", 400);

        TradeRequest tradeRequest = new TradeRequest(Ticker.GOOGLE, TradeAction.BUY, 2);
        postTradeRequest(tradeRequest, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Customer [id=1] does not have enough funds to complete the transaction");

    }

    @Test
    public void inputValidation() {

        TradeRequest missingTicker = new TradeRequest(null, TradeAction.BUY, 2);
        postTradeRequest(missingTicker, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Ticker is required");

        TradeRequest missingAction = new TradeRequest(Ticker.GOOGLE, null, 2);
        postTradeRequest(missingAction, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Trade action is required");

        TradeRequest invalidQuantity = new TradeRequest(Ticker.GOOGLE, TradeAction.BUY, 0);
        postTradeRequest(invalidQuantity, HttpStatus.BAD_REQUEST)
                .jsonPath("$.detail").isEqualTo("Valid quantity is required");

    }

    private void mockCustomerTrade(String path, int responseCode) {
        String stockResponseBody = this.resourceToString("stock-service/stock-price-200.json");
        mockServerClient
                .when(HttpRequest.request("/stock/GOOGLE"))
                .respond(
                        HttpResponse.response(stockResponseBody)
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                );

        String customerResponseBody = this.resourceToString(path);
        mockServerClient
                .when(
                        HttpRequest.request("/customers/1/trade")
                                .withMethod("POST")
                                .withBody(RegexBody.regex(".*\"price\":110.*"))
                )
                .respond(
                        HttpResponse.response(customerResponseBody)
                                .withStatusCode(responseCode)
                                .withContentType(MediaType.APPLICATION_JSON)
                );
    }

    private WebTestClient.BodyContentSpec postTradeRequest(TradeRequest tradeRequest, HttpStatus expectedStatus) {
        return this.webTestClient.post()
                .uri("/customers/1/trade")
                .bodyValue(tradeRequest)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody()
                .consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
    }

}
