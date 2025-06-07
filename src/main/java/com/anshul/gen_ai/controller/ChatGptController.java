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
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/chatgpt")
@RestController
public class ChatGptController {

    private final ChatClient chatClient;

    public ChatGptController(OpenAiChatModel chatModel) {
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

    @GetMapping("/info")
    public Map<String, Object> findPopularPlayersMapped(@RequestParam("sport") String sport) {
        MapOutputConverter outputConverter = new MapOutputConverter();
        PromptTemplate template = new PromptTemplate(PromptUtility.popularPlayersInfoPrompt());
        Prompt prompt = template.create(Map.of("sports", sport, "format", outputConverter.getFormat()));
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        System.out.println(response.getMetadata().getModel());
        return outputConverter.convert(response.getResult().getOutput().getText());
    }

    @GetMapping("/details")
    public List<PlayerDetails> findPlayerDetails(@RequestParam("sport") String sport) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        BeanOutputConverter<List<PlayerDetails>> outputConverter =
                new BeanOutputConverter<>(new ParameterizedTypeReference<List<PlayerDetails>>() {}, objectMapper);
        PromptTemplate template = new PromptTemplate(PromptUtility.popularPlayersDetailsPrompt());
        Prompt prompt = template.create(Map.of("sports", sport, "format", outputConverter.getFormat()));
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        System.out.println(response.getMetadata().getModel());
        return outputConverter.convert(response.getResult().getOutput().getText());
    }
}
