package com.example.reactive_stock_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;
    private final Random random = new Random();

    // Spring automatically injects our StockService here
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // 1. Single Stock Endpoint
    // Returns Mono because we are fetching exactly 0 or 1 item
    @GetMapping("/{symbol}")
    public Mono<StockPrice> getSingleStock(@PathVariable String symbol) {
        String upperSymbol = symbol.toUpperCase();
        
        // Requirement: Return 404 if the stock is not found
        if (!stockService.isValidSymbol(upperSymbol)) {
            return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));
        }
        
        // Requirement: Return the stock price with current timestamp
        return Mono.just(new StockPrice(
                upperSymbol, 
                stockService.getBasePrice(upperSymbol), 
                System.currentTimeMillis()
        ));
    }

    // 2. Multiple Stocks Endpoint
    // Returns Flux because we are fetching a list (0 to N items)
    @GetMapping
    public Flux<StockPrice> getMultipleStocks(@RequestParam String symbols) {
        // We split the comma-separated string into a list and turn it into a Flux stream
        return Flux.fromIterable(Arrays.asList(symbols.split(",")))
                .map(String::toUpperCase)
                .filter(stockService::isValidSymbol) // Requirement: Ignore invalid symbols
                .map(sym -> new StockPrice(
                        sym, 
                        stockService.getBasePrice(sym), 
                        System.currentTimeMillis()
                ));
    }

    // 3. SSE Stream Endpoint
    // Returns a continuous Flux stream of ServerSentEvents
    @GetMapping(value = "/stream/{symbol}", produces = "text/event-stream")
    public Flux<ServerSentEvent<StockPrice>> streamStockPrices(@PathVariable String symbol) {
        String upperSymbol = symbol.toUpperCase();
        
        if (!stockService.isValidSymbol(upperSymbol)) {
            return Flux.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found"));
        }

        Double basePrice = stockService.getBasePrice(upperSymbol);

        // Requirement: Emit a new event every 2 seconds
        return Flux.interval(Duration.ofSeconds(2))
                .map(sequence -> {
                    // Requirement: Randomly fluctuate the price within ±5%
                    double fluctuation = basePrice * 0.05 * (random.nextDouble() * 2 - 1);
                    double newPrice = Math.round((basePrice + fluctuation) * 100.0) / 100.0;
                    
                    StockPrice priceUpdate = new StockPrice(upperSymbol, newPrice, System.currentTimeMillis());
                    
                    // Wrap the data in a ServerSentEvent object
                    return ServerSentEvent.<StockPrice>builder()
                            .id(String.valueOf(sequence))
                            .event("price-update")
                            .data(priceUpdate)
                            .build();
                });
    }
}