package com.aimock.interview.interview.ai.conversation.service;

import com.aimock.interview.interview.ai.conversation.AiProvider.GroqInterviewAiClient;
import com.aimock.interview.interview.ai.conversation.context.InterviewAiContext;
import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.aimock.interview.interview.ai.conversation.prompt.InterviewAiPromptRules;
import com.aimock.interview.interview.commons.enums.InterviewType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HrInterviewAiService implements InterviewAiService {

    private final GroqInterviewAiClient groqInterviewAiClient;

    @Override
    public InterviewType getInterviewType() {
        return InterviewType.HR;
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

                This is an HR interview.

                Focus on:

                - communication
                - motivation
                - career goals
                - teamwork
                - conflict resolution
                - adaptability
                - leadership
                - workplace behavior
                - strengths and weaknesses
                - role alignment

                Evaluate how the candidate communicates,
                thinks, behaves, and handles workplace situations.

                The target role and experience level provide
                additional context for the interview.


                ==================================================
                HR FOLLOW-UP BEHAVIOR
                ==================================================

                Prefer FOLLOW_UP when the candidate's answer
                contains a specific experience, decision,
                behavior, or situation worth exploring.

                The follow-up must reference something explicitly
                mentioned by the candidate.

                Ask specific behavioral follow-ups.

                Do not ask vague questions such as:

                "Could you elaborate?"

                "Can you tell me more?"

                Instead, ask directly about the specific
                experience, decision, or situation mentioned
                by the candidate.
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