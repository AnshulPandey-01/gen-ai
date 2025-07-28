package com.anshul.gen_ai.services;

import com.anshul.gen_ai.config.StockMarketConfigProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

public class StockMarketService implements Function<StockMarketService.Request, StockMarketService.Response> {

    private final StockMarketConfigProperties stockMarketConfigProperties;

    public StockMarketService(StockMarketConfigProperties stockMarketConfigProperties) {
        this.stockMarketConfigProperties = stockMarketConfigProperties;
    }

    @Override
    public Response apply(Request request) {
        System.out.println("Stock Market Request for " + request.stockName());

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-rapidapi-host", stockMarketConfigProperties.getHost());
        headers.set("x-rapidapi-key", stockMarketConfigProperties.getKey());

        return RestClient.create(stockMarketConfigProperties.getUrl())
                .get()
                .uri(uriBuilder -> uriBuilder.path("/stock").queryParam("name", request.stockName()).build())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .retrieve()
                .body(Response.class);
    }

    public record Request(String stockName) {}

    public record Response(String companyName, CurrentPrice currentPrice, double yearHigh, double yearLow) {}

    public record CurrentPrice(double BSE, double NSE) {}

}
