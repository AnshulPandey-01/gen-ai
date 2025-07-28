package com.anshul.gen_ai.config;

import com.anshul.gen_ai.services.StockMarketService;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.Resource;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Configuration
public class Config {

    @Value("classpath:/budget_speech.txt")
    private Resource budget;

    private final StockMarketConfigProperties stockMarketConfigProperties;

    public Config(StockMarketConfigProperties stockMarketConfigProperties) {
        this.stockMarketConfigProperties = stockMarketConfigProperties;
    }

    @Bean
    SimpleVectorStore simpleVectorStore(@Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) throws URISyntaxException {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        URL url = getClass().getClassLoader().getResource("vector_store.json");
        if (url != null) {
            System.out.println("Loading vector store file");
            simpleVectorStore.load(Path.of(url.toURI()).toFile());
        } else {
            System.out.println("Create Vector Store File");
            TextReader textReader = new TextReader(budget);
            textReader.getCustomMetadata().put("filename", budget.getFilename());
            List<Document> documents = textReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(Paths.get("src\\main\\resources\\vector_store.json").toFile());
        }
        return simpleVectorStore;
    }

    @Bean
    @Description("Get stock price for the given stock name")
    public Function<StockMarketService.Request, StockMarketService.Response> getStockPrice() {
        return new StockMarketService(stockMarketConfigProperties);
    }
}
