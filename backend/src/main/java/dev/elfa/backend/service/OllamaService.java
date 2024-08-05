package dev.elfa.backend.service;

import dev.elfa.backend.model.personality.Personality;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptionsBuilder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class OllamaService {
    private final ChatClient chatClient;

    OllamaService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultOptions(ChatOptionsBuilder.builder()
                        .withTemperature(0.9f)
                        .build())
                .build();
    }

    public String createTweet(Personality personality) {
        String tone = personality.tone().stream().map(Enum::name).collect(Collectors.joining(", ")).toLowerCase();
        String interest = personality.interest().stream().map(Enum::name).collect(Collectors.joining(", ")).toLowerCase();

        String systemPrompt = String.format("""
                You are a content creation expert on Twitter. Write in the tone %s.
                Create tweets that spark conversations, include calls to action, and use hashtags and keywords.
                The tweet must be 280 characters or less. Here are some examples of good tweets:
                1. "Excited to share my latest project! 🚀 Check it out and let me know what you think! #innovation #tech"
                2. "Just finished a great book on leadership. What are your favorite reads? 📚 #leadership #reading"
                Ensure the tweet is concise, engaging, and relevant to the specified interests. Only include the tweet in your response.
                """, tone);

        String userPrompt = String.format("""
                Could you assist me in drafting a tweet that engages my target audience in one of these topics: %s?
                """, interest);

        String response = chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();

        return response.length() > 280 ? response.substring(0, 280) : response;
    }
}
