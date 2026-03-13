package com.example.reactive_stock_service;

public record StockPrice(String symbol, Double price, Long timestamp) {
}