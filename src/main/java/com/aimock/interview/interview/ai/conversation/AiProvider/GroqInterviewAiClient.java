package com.aimock.interview.interview.ai.conversation.AiProvider;

import com.aimock.interview.interview.ai.conversation.context.InterviewAiContext;
import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroqInterviewAiClient {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiInterviewResponse generate(
            String systemPrompt,
            String userPrompt) {
        return generate(systemPrompt, userPrompt, null);
    }

    public AiInterviewResponse generate(
            String systemPrompt,
            String userPrompt,
            InterviewAiContext context) {

        log.info("Generating dynamic AI interview question via Groq LLM...");

        // Single Groq call — get raw string first, then parse
        String rawContent;
        try {
            var response = chatClient
                    .prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call();

            log.info("Groq response: {}", response);

            rawContent = response.content();

            log.info("Groq raw content: [{}]", rawContent);

        } catch (Exception ex) {
            throw new RuntimeException("Groq AI call failed: " + ex.getMessage(), ex);
        }

        if (rawContent == null || rawContent.isBlank()) {
            throw new RuntimeException("Groq AI returned empty response");
        }

        // Try clean JSON parse first (most common path)
        try {
            String cleanJson = extractJson(rawContent);
            return objectMapper.readValue(cleanJson, AiInterviewResponse.class);
        } catch (Exception jsonEx) {
            log.warn("Raw JSON parse failed ({}). Trying Spring AI entity mapping...", jsonEx.getMessage());
        }

        // Fallback: re-wrap content into Spring AI entity binding
        try {
            return chatClient
                    .prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .entity(AiInterviewResponse.class);
        } catch (Exception fallbackEx) {
            log.error("All Groq parse strategies failed. Raw response was: {}", rawContent);
            throw new RuntimeException("Groq AI interview generation failed: " + fallbackEx.getMessage(), fallbackEx);
        }
    }

    private String extractJson(String text) {
        if (text == null) {
            return "";
        }
        String trimmed = text.trim();
        int firstBrace = trimmed.indexOf('{');
        int lastBrace = trimmed.lastIndexOf('}');
        if (firstBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1).trim();
        }
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        return trimmed.trim();
    }
}