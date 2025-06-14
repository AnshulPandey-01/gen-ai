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
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RequestMapping("/chatgpt")
@RestController
public class ChatGptController {

    @Autowired
    private OpenAiImageModel openAiImageModel;

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

    @GetMapping("/image/generate")
    public String generateImage(@RequestParam("prompt") String prompt) {
        ImageResponse response = openAiImageModel.call(
                new ImagePrompt(
                        prompt,
                        OpenAiImageOptions.builder().height(1024).width(1792).quality("hd").N(1).build()
                )
        );
        return response.getResult().getOutput().getUrl();
    }

    @GetMapping("/image/toText")
    public String imageToText(@RequestParam("jpegImage") MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("tempUpload_", "_" + multipartFile.getOriginalFilename());
        multipartFile.transferTo(tempFile);
        return chatClient
                .prompt()
                .user(promptUserSpec ->
                        promptUserSpec
                                .text(PromptUtility.explainImage())
                                .media(MimeTypeUtils.IMAGE_JPEG, new FileSystemResource(tempFile))
                ).call().content();
    }
}
