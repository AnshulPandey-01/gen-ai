package com.anshul.gen_ai.config;

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
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

@Configuration
public class Config {

    @Value("classpath:/budget_speech.txt")
    private Resource budget;

    @Bean
    SimpleVectorStore simpleVectorStore(@Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        File vectorStoreFile = new File("D:\\Java Projects\\Idea Projects\\gen-ai\\src\\main\\resources\\vector_store.json");
        if (vectorStoreFile.exists()) {
            System.out.println("Loading vector store file");
            simpleVectorStore.load(vectorStoreFile);
        } else {
            System.out.println("Create Vector Store File");
            TextReader textReader = new TextReader(budget);
            textReader.getCustomMetadata().put("filename", budget.getFilename());
            List<Document> documents = textReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }
        return simpleVectorStore;
    }
}
