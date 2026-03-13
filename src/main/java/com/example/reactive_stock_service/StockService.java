package com.example.reactive_stock_service;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class StockService {

    // This is our hardcoded "database" containing the exact required seed data
    private final Map<String, Double> stockBasePrices = Map.of(
            "AAPL", 150.00,
            "GOOG", 2800.00,
            "MSFT", 300.00,
            "AMZN", 3400.00,
            "TSLA", 700.00
    );

    // A helper method to easily fetch a price by its symbol
    public Double getBasePrice(String symbol) {
        return stockBasePrices.get(symbol);
    }

    // A helper method to check if we track a specific stock
    public boolean isValidSymbol(String symbol) {
        return stockBasePrices.containsKey(symbol);
    }
}