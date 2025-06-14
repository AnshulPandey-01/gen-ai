package com.anshul.gen_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RagController {

    private final ChatClient chatClient;

    public RagController(OpenAiChatModel chatModel, VectorStore vectorStore) {
        this.chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
    }

    @GetMapping("/budget-queries")
    public String budgetQAndA(@RequestParam("question") String question) {
        System.out.println("Budget Question: " + question);
        return chatClient.prompt().user(question).call().content();
    }
}
