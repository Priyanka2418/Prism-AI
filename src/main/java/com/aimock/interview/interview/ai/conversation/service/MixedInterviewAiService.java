package com.aimock.interview.interview.ai.conversation.service;

import com.aimock.interview.interview.ai.conversation.AiProvider.GroqInterviewAiClient;
import com.aimock.interview.interview.ai.conversation.context.InterviewAiContext;
import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.aimock.interview.interview.ai.conversation.prompt.InterviewAiPromptRules;
import com.aimock.interview.interview.commons.enums.InterviewType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MixedInterviewAiService implements InterviewAiService {

    private final GroqInterviewAiClient groqInterviewAiClient;

    @Override
    public InterviewType getInterviewType() {
        return InterviewType.MIXED;
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
                INTERVIEW TYPE: MIXED (TECHNICAL + BEHAVIORAL)
                ==================================================

                This is a comprehensive mixed interview that alternates between technical and behavioral questions.

                Alternation strategy:
                - Broadly alternate between technical and behavioral question styles across topics.
                - Do NOT ask two consecutive technical questions AND do NOT ask two consecutive behavioral questions.
                - Use the RECENT CONVERSATION and DIFFICULTY PROGRESSION to detect whether the last question was
                  technical or behavioral, then switch to the other type for the next turn.

                Technical question guidelines:
                - Focus on core concepts, implementation details, debugging, architecture, and trade-offs.
                - Align depth with the candidate's target role and experience level.

                Behavioral question guidelines (STAR method):
                - Frame as "Tell me about a time when..." or "Give me an example of..."
                - Assess communication, ownership, teamwork, conflict resolution, and adaptability.
                - Probe for concrete Situation → Task → Action → Result structure.

                Bridging between types:
                - When switching from technical to behavioral, make a natural transition:
                  e.g. "Let's take a step back from the technical side — tell me about a time you had to..."
                - When switching from behavioral to technical:
                  e.g. "Great. Now let's dive back into the technical topics — how would you approach..."

                Align overall question depth and complexity with the candidate's experience level:
                - JUNIOR / INTERN: Basic technical concepts + entry-level behavioral scenarios (teamwork, learning)
                - MID-LEVEL: Design patterns + cross-team collaboration and ownership
                - SENIOR / LEAD: Architecture + organizational influence, technical leadership
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
