package com.anshul.gen_ai;

import com.anshul.gen_ai.config.StockMarketConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(StockMarketConfigProperties.class)
@SpringBootApplication
public class GenAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenAiApplication.class, args);
	}

}
