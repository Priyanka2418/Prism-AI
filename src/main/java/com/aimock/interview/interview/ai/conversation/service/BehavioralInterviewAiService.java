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
public class BehavioralInterviewAiService implements InterviewAiService {

    private final GroqInterviewAiClient groqInterviewAiClient;

    @Override
    public InterviewType getInterviewType() {
        return InterviewType.BEHAVIORAL;
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
                INTERVIEW TYPE: BEHAVIORAL
                ==================================================

                Focus exclusively on behavioral questions using the STAR method (Situation, Task, Action, Result):
                - Ask the candidate to share real past experiences and concrete examples.
                - Probe how they handled challenges, conflicts, ambiguity, or high-pressure situations.
                - Assess soft skills: communication, ownership, adaptability, teamwork, and growth mindset.
                - Evaluate emotional intelligence: empathy, self-awareness, and conflict resolution.
                - Explore motivation, values, and alignment with engineering culture.

                Behavioral question guidelines:
                - Always frame questions as "Tell me about a time when..." or "Give me an example of..."
                - When the candidate gives a vague or surface-level answer, follow up by anchoring to STAR:
                  e.g. "What was the specific outcome?" or "What action did YOU personally take?"
                - Align question complexity with the candidate's experience level:
                  * JUNIOR / INTERN: Focus on teamwork, learning from mistakes, handling feedback.
                  * MID-LEVEL: Focus on cross-team collaboration, driving results, managing ambiguity.
                  * SENIOR / LEAD: Focus on influence without authority, organizational change, mentoring others.

                Do NOT ask technical or coding questions. This is a pure behavioral assessment.
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
