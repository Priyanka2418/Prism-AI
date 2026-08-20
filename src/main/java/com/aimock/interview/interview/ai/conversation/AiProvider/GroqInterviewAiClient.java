package com.aimock.interview.interview.ai.conversation.AiProvider;

import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroqInterviewAiClient {

    private final ChatClient chatClient;

    public AiInterviewResponse generate(
            String systemPrompt,
            String userPrompt) {

        return chatClient
                .prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .entity(
                        AiInterviewResponse.class,
                        spec -> spec.validateSchema()
                );
    }
}