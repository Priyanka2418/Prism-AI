package com.aimock.interview.interview.turn.factory;

import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.aimock.interview.interview.commons.enums.AiAction;
import com.aimock.interview.interview.commons.enums.Speaker;
import com.aimock.interview.interview.commons.enums.TurnType;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.turn.dto.SubmitAnswerRequest;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InterviewTurnFactory {

    private static final String OPENING_QUESTION =
            "Tell me about yourself.";

    public InterviewTurn createOpeningQuestion(
            Interview interview) {

        InterviewTurn turn = new InterviewTurn();

        turn.setInterview(interview);
        turn.setTurnNumber(1);
        turn.setSpeaker(Speaker.AI);
        turn.setTurnType(TurnType.QUESTION);
        turn.setContent(OPENING_QUESTION);
        turn.setDifficulty(interview.getInterviewDifficulty());
        turn.setAiAction(AiAction.NEW_TOPIC);
        turn.setStartedAt(LocalDateTime.now());

        return turn;
    }

    public InterviewTurn createCandidateAnswer(
            Interview interview,
            InterviewTurn questionTurn,
            SubmitAnswerRequest request) {

        LocalDateTime now = LocalDateTime.now();

        InterviewTurn answer = new InterviewTurn();

        answer.setInterview(interview);
        answer.setTurnNumber(
                questionTurn.getTurnNumber() + 1);
        answer.setSpeaker(Speaker.CANDIDATE);
        answer.setTurnType(TurnType.ANSWER);
        answer.setContent(request.content());
        answer.setParentTurn(questionTurn);
        answer.setTopic(questionTurn.getTopic());
        answer.setDifficulty(questionTurn.getDifficulty());
        answer.setAnswerDurationSeconds(
                request.answerDurationSeconds());
        answer.setStartedAt(now);
        answer.setCompletedAt(now);

        return answer;
    }

    public InterviewTurn createAiTurn(
            Interview interview,
            InterviewTurn candidateAnswer,
            AiInterviewResponse aiResponse,
            boolean closingMode) {

        InterviewTurn turn = new InterviewTurn();

        turn.setInterview(interview);
        turn.setTurnNumber(
                candidateAnswer.getTurnNumber() + 1);
        turn.setSpeaker(Speaker.AI);
        turn.setContent(aiResponse.content());
        turn.setParentTurn(candidateAnswer);
        turn.setStartedAt(LocalDateTime.now());

        if (closingMode) {

            turn.setTurnType(TurnType.CLOSING);

        } else {

            turn.setTurnType(TurnType.QUESTION);
            turn.setTopic(aiResponse.topic());
            turn.setDifficulty(aiResponse.difficulty());
            turn.setAiAction(aiResponse.aiAction());
        }

        return turn;
    }
}