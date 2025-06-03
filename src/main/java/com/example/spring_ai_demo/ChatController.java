package com.example.spring_ai_demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ChatController {
    private final ChatClient chatClient;
    private final ImageModel imageModel;

    public ChatController(ChatClient chatClient, ImageModel imageModel) {
        this.chatClient = chatClient;
        this.imageModel = imageModel;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String prompt) {
        return chatClient.prompt().user(prompt).call().content();
    }

    @PostMapping("/image")
    public String generateImage(@RequestBody String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse imageResponse = imageModel.call(imagePrompt);
        Image image = imageResponse.getResult().getOutput();
        return Optional.ofNullable(image.getUrl()).orElseGet(image::getB64Json);
    }
}
