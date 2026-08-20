
//internal domain/context object used by the AI conversation subsystem.


package com.aimock.interview.interview.ai.conversation.context;

import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.turn.entity.InterviewTurn;

import java.util.List;

public record InterviewAiContext(
        Interview interview,
        List<InterviewTurn> previousTurns,
        InterviewTurn candidateAnswer,
        List<InterviewTurn> allQuestions,
        String coveredTopics,
        String currentTopic,
        String questionsAlreadyAsked,
        String difficultyProgression,
        String recentConversation,
        String recentCandidatePoints,
        int consecutiveFollowUps,
        boolean closingMode
) {
}
