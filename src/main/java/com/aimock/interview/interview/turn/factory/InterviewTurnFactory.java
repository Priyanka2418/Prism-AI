package com.aimock.interview.interview.turn.factory;

import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.aimock.interview.interview.commons.enums.AiAction;
import com.aimock.interview.interview.commons.enums.InterviewType;
import com.aimock.interview.interview.commons.enums.Speaker;
import com.aimock.interview.interview.commons.enums.TurnType;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.turn.dto.SubmitAnswerRequest;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InterviewTurnFactory {

    public InterviewTurn createOpeningQuestion(
            Interview interview) {

        InterviewTurn turn = new InterviewTurn();

        turn.setInterview(interview);
        turn.setTurnNumber(1);
        turn.setSpeaker(Speaker.AI);
        turn.setTurnType(TurnType.QUESTION);
        turn.setContent(generateOpeningQuestion(interview));
        turn.setTopic(getInitialTopic(interview.getInterviewType()));
        turn.setDifficulty(interview.getInterviewDifficulty());
        turn.setAiAction(AiAction.NEW_TOPIC);
        turn.setStartedAt(LocalDateTime.now());

        return turn;
    }

    private String getInitialTopic(InterviewType interviewType) {
        if (interviewType == null) {
            return "Introduction & Technical Background";
        }
        return switch (interviewType) {
            case HR -> "Introduction & Background";
            case BEHAVIORAL -> "Introduction & Professional Background";
            case MIXED -> "Introduction & Overview";
            case TECHNICAL -> "Introduction & Technical Background";
        };
    }

    private String generateOpeningQuestion(Interview interview) {
        String role = interview.getTargetRole() != null ? interview.getTargetRole() : "Software Engineer";
        InterviewType type = interview.getInterviewType() != null ? interview.getInterviewType() : InterviewType.TECHNICAL;

        return switch (type) {
            case HR -> "Welcome! Please introduce yourself and share a brief overview of your background and experience.";
            case BEHAVIORAL -> "Welcome to your " + role + " behavioral interview! To begin, could you introduce yourself and tell me a bit about your professional journey and key accomplishments?";
            case MIXED -> "Welcome to your " + role + " interview! To start off, could you briefly introduce yourself and give an overview of your background and the tech stack you work with?";
            case TECHNICAL -> "Welcome to your " + role + " technical interview! To begin, could you briefly introduce yourself and describe the technical stack you've worked most with?";
        };
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
            turn.setTopic(aiResponse.topic() != null ? aiResponse.topic() : "Wrap-up & Conclusion");
            turn.setDifficulty(aiResponse.difficulty() != null ? aiResponse.difficulty() : interview.getInterviewDifficulty());
            turn.setAiAction(aiResponse.aiAction() != null ? aiResponse.aiAction() : AiAction.END_INTERVIEW);
        } else {
            turn.setTurnType(TurnType.QUESTION);
            turn.setTopic(aiResponse.topic() != null ? aiResponse.topic() : "Discussion");
            turn.setDifficulty(aiResponse.difficulty() != null ? aiResponse.difficulty() : interview.getInterviewDifficulty());
            turn.setAiAction(aiResponse.aiAction() != null ? aiResponse.aiAction() : AiAction.NEW_TOPIC);
        }

        return turn;
    }
}