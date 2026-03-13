package com.example.reactive_stock_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class ReactiveRequestLoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(ReactiveRequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        ServerHttpRequest request = exchange.getRequest();

        // We pass the request down the chain, and ONLY when it is completely finished (doFinally), 
        // we calculate the time and log it. This guarantees we don't block the thread!
        return chain.filter(exchange).doFinally(signalType -> {
            // The requirement asks us to specifically log requests to the /api/ path
            if (request.getPath().value().startsWith("/api/")) {
                long duration = System.currentTimeMillis() - startTime;
                log.info("Request: {} {}, Duration: {}ms", request.getMethod(), request.getPath(), duration);
            }
        });
    }
}