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
                buildUserPrompt(context)
        );
    }

    private String buildSystemPrompt() {

        return InterviewAiPromptRules.COMMON_RULES + """

                ==================================================
                INTERVIEW TYPE
                ==================================================

                This is a TECHNICAL interview.

                Focus on:

                - technical concepts
                - implementation
                - debugging
                - architecture
                - APIs
                - databases
                - algorithms
                - technical problem solving

                The target role provides additional context,
                but technical interview behavior is authoritative.


                ==================================================
                TECHNICAL FOLLOW-UP BEHAVIOR
                ==================================================

                Prefer FOLLOW_UP when the candidate's answer
                contains a meaningful technical detail worth exploring.

                The follow-up must reference a specific technical
                detail explicitly mentioned by the candidate.

                Do not ask vague questions such as:

                Instead, ask directly about the specific
                technical detail.
                """;
    }

    private String buildUserPrompt(
            InterviewAiContext context) {

        var interview = context.interview();

        return """
                ==================================================
                INTERVIEW CONFIGURATION
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
                INTERVIEW PROGRESS
                ==================================================

                Covered topics:
                %s

                Current topic:
                %s

                Difficulty progression:
                %s


                ==================================================
                QUESTIONS ALREADY ASKED
                ==================================================

                %s


                ==================================================
                RECENT CONVERSATION
                ==================================================

                %s


                ==================================================
                IMPORTANT RECENT CANDIDATE POINTS
                ==================================================

                %s


                ==================================================
                LATEST CANDIDATE ANSWER
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
                context.difficultyProgression(),
                context.questionsAlreadyAsked(),
                context.recentConversation(),
                context.recentCandidatePoints(),
                context.candidateAnswer().getContent()
        );
    }
}