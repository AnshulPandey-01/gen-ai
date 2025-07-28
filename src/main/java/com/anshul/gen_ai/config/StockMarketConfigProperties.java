package com.anshul.gen_ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("x-rapidapi")
public record StockMarketConfigProperties(String key, String host, String url) {
    public String getUrl() {
        return url;
    }
    public String getKey() {
        return key;
    }
    public String getHost() {
        return host;
    }
}
