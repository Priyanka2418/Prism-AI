package com.aimock.interview.interview.ai.conversation.service;

import com.aimock.interview.interview.ai.conversation.AiProvider.GroqInterviewAiClient;
import com.aimock.interview.interview.ai.conversation.context.InterviewAiContext;
import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.aimock.interview.interview.ai.conversation.prompt.InterviewAiPromptRules;
import com.aimock.interview.interview.ai.conversation.service.InterviewAiService;
import com.aimock.interview.interview.commons.enums.InterviewType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TechnicalInterviewAiService implements InterviewAiService {

    private final GroqInterviewAiClient groqInterviewAiClient;

    @Override
    public InterviewType getInterviewType() {
        return InterviewType.TECHNICAL;
    }

    @Override
    public AiInterviewResponse generateNextQuestion(
            InterviewAiContext context) {

        return groqInterviewAiClient.generate(
                buildSystemPrompt(),
                buildUserPrompt(context),
                context
        );
    }

    private String buildSystemPrompt() {

        return InterviewAiPromptRules.COMMON_RULES + """

                ==================================================
                INTERVIEW TYPE: TECHNICAL
                ==================================================

                Focus on:
                - core concepts and practical implementation
                - architecture and data flow
                - APIs, databases, caching, and concurrency
                - debugging, failure modes, and optimization trade-offs

                Align question difficulty and scope strictly with the candidate's target role and experience level.
                """;
    }

    private String buildUserPrompt(
            InterviewAiContext context) {

        var interview = context.interview();

        return """
                ==================================================
                INTERVIEW CONFIGURATION (FROM CANDIDATE FORM)
                ==================================================

                Target role:
                %s

                Experience level:
                %s

                Interview type:
                %s

                Configured difficulty:
                %s

                Selected topics:
                %s


                ==================================================
                INTERVIEW PROGRESS & STATE
                ==================================================

                Covered topics:
                %s

                Current topic:
                %s

                Consecutive follow-ups on current topic:
                %d (CRITICAL: If >= 1, aiAction MUST be NEW_TOPIC selecting from unvisited topics)

                Closing mode active:
                %s (If true, aiAction MUST be END_INTERVIEW with a polite closing wrap-up)

                Difficulty progression:
                %s


                ==================================================
                QUESTIONS ALREADY ASKED (DO NOT REPEAT)
                ==================================================

                %s

                ==================================================
                RECENT CONVERSATION
                ==================================================

                %s


                ==================================================
                LATEST CANDIDATE ANSWER (EVALUATE THIS DEEPLY)
                ==================================================

                %s
                """.formatted(
                interview.getTargetRole(),
                interview.getExperienceLevel(),
                interview.getInterviewType(),
                interview.getInterviewDifficulty(),
                interview.getTopics(),
                context.coveredTopics(),
                context.currentTopic(),
                context.consecutiveFollowUps(),
                context.closingMode(),
                context.difficultyProgression(),
                context.questionsAlreadyAsked(),
                context.recentConversation(),
                context.candidateAnswer() != null && context.candidateAnswer().getContent() != null
                        ? context.candidateAnswer().getContent()
                        : "(First turn / starting interview)"
        );
    }
}