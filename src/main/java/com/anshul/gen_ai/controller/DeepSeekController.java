package com.anshul.gen_ai.controller;

import com.anshul.gen_ai.dto.PlayerDetails;
import com.anshul.gen_ai.utility.PromptUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/deepseek")
@RestController
public class DeepSeekController {

    private final ChatClient chatClient;

    public DeepSeekController(OllamaChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping("")
    public String findPopularPlayers(@RequestParam("sport") String sport) {
        PromptTemplate template = new PromptTemplate(PromptUtility.popularPlayersPrompt());
        Prompt prompt = template.create(Map.of("sports", sport));
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        System.out.println(response.getMetadata().getModel());
        return response.getResult().getOutput().getText();
    }
}
