package com.example.reactive_stock_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class StockControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getSingleStock_ValidSymbol_ReturnsOk() {
        webTestClient.get().uri("/api/stocks/AAPL")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.symbol").isEqualTo("AAPL")
                .jsonPath("$.price").isEqualTo(150.0)
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void getSingleStock_InvalidSymbol_ReturnsNotFound() {
        webTestClient.get().uri("/api/stocks/UNKNOWN")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getMultipleStocks_MixedSymbols_IgnoresInvalid() {
        // Requesting AAPL (Valid), MSFT (Valid), and UNKNOWN (Invalid)
        webTestClient.get().uri("/api/stocks?symbols=AAPL,UNKNOWN,MSFT")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].symbol").isEqualTo("AAPL")
                .jsonPath("$[1].symbol").isEqualTo("MSFT")
                .jsonPath("$.length()").isEqualTo(2); // Ensures the unknown symbol was ignored
    }
}