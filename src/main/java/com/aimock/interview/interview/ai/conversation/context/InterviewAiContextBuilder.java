package com.aimock.interview.interview.ai.conversation.context;

import com.aimock.interview.interview.commons.enums.AiAction;
import com.aimock.interview.interview.commons.enums.Speaker;
import com.aimock.interview.interview.commons.enums.TurnType;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import com.aimock.interview.interview.turn.repository.InterviewTurnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class InterviewAiContextBuilder {

    private final InterviewTurnRepository interviewTurnRepository;


    public InterviewAiContext build(
            Interview interview,
            List<InterviewTurn> previousTurns,
            InterviewTurn candidateAnswer,
            boolean closingMode) {

        /*
         Retrieve all questions asked so far.

         This provides a compact representation of the overall
         interview progress without sending the complete conversation
         to the LLM.
         */
        List<InterviewTurn> allQuestions =
                interviewTurnRepository
                        .findByInterviewIdAndTurnTypeOrderByTurnNumberAsc(
                                interview.getId(),
                                TurnType.QUESTION);


         //Topics that have already appeared during the interview.

        String coveredTopics = allQuestions.stream()
                .map(InterviewTurn::getTopic)
                .filter(topic -> topic != null && !topic.isBlank())
                .distinct()
                .collect(Collectors.joining(", "));


        // Most recently discussed topic.

        String currentTopic = allQuestions.stream()
                .map(InterviewTurn::getTopic)
                .filter(topic -> topic != null && !topic.isBlank())
                .reduce((first, second) -> second)
                .orElse("Not established yet");


        // Every content already asked.
         //Used by the LLM to avoid repeating questions.

        String questionsAlreadyAsked = allQuestions.stream()
                .map(turn -> "- " + turn.getContent())
                .collect(Collectors.joining("\n"));


         // Shows how interview difficulty has progressed.

        String difficultyProgression = allQuestions.stream()
                .map(turn -> String.format(
                        "Q%d: %s",
                        turn.getTurnNumber(),
                        turn.getDifficulty()))
                .collect(Collectors.joining(" -> "));


         // Only the recent conversation window is included.

         // previousTurns is already limited by InterviewTurnService.

        String recentConversation = previousTurns.stream()
                .map(turn -> "%s: %s".formatted(
                        turn.getSpeaker(),
                        turn.getContent()))
                .collect(Collectors.joining("\n"));


         // Extract candidate statements from the recent context.
         // These are useful for generating specific follow-up questions.

        String recentCandidatePoints = previousTurns.stream()
                .filter(turn -> turn.getSpeaker() == Speaker.CANDIDATE)

                .map(turn -> "- " + turn.getContent())
                .collect(Collectors.joining("\n"));

        InterviewTurn latestQuestion = allQuestions.isEmpty()
                ? null
                : allQuestions.get(allQuestions.size() - 1);

        int consecutiveFollowUps =
                latestQuestion != null
                        && latestQuestion.getAiAction() == AiAction.FOLLOW_UP
                        ? 1
                        : 0;

        return new InterviewAiContext(
                interview,
                previousTurns,
                candidateAnswer,
                allQuestions,
                coveredTopics,
                currentTopic,
                questionsAlreadyAsked,
                difficultyProgression,
                recentConversation,
                recentCandidatePoints,
                consecutiveFollowUps,
                closingMode);
    }
}