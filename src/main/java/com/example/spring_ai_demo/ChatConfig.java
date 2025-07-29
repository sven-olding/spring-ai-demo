package com.example.spring_ai_demo;

import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import io.modelcontextprotocol.client.McpSyncClient;

@Configuration
public class ChatConfig {

    @Value("classpath:/prompts/system.st")
    private Resource systemPrompt;

    @Bean
    ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            List<McpSyncClient> mcpSyncClients,
            VectorStore vectorStore) {

        var qaAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .similarityThreshold(0.6d)
                        .topK(5)
                        .build())
                .build();

        var chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return chatClientBuilder
                .defaultAdvisors(List.of(qaAdvisor, chatMemoryAdvisor))
                .defaultSystem(systemPrompt)
                .defaultOptions(ChatOptions.builder()
                        .temperature(0.2) // controls randomness
                        .maxTokens(512) // caps output length
                        .topP(0.9) // Nucleus sampling, consider tokens from top n % probability
                        .build())
                .defaultTools(new ChatTools())
                .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
                .build();
    }
}
