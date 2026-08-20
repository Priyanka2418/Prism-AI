package com.aimock.interview.interview.turn.service;

import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.InvalidStateException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.interview.ai.conversation.context.InterviewAiContext;
import com.aimock.interview.interview.ai.conversation.context.InterviewAiContextBuilder;
import com.aimock.interview.interview.ai.conversation.dto.AiInterviewResponse;
import com.aimock.interview.interview.ai.conversation.service.InterviewAiService;
import com.aimock.interview.interview.ai.conversation.service.InterviewAiServiceResolver;
import com.aimock.interview.interview.commons.InterviewSecurity;
import com.aimock.interview.interview.commons.enums.AiAction;
import com.aimock.interview.interview.commons.enums.InterviewStatus;
import com.aimock.interview.interview.commons.enums.Speaker;
import com.aimock.interview.interview.commons.enums.TurnType;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.lifecycle.repository.InterviewRepository;
import com.aimock.interview.interview.turn.dto.InterviewTurnResponse;
import com.aimock.interview.interview.turn.dto.SubmitAnswerRequest;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import com.aimock.interview.interview.turn.factory.InterviewTurnFactory;
import com.aimock.interview.interview.turn.mapper.InterviewTurnMapper;
import com.aimock.interview.interview.turn.repository.InterviewTurnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewTurnServiceImpl implements InterviewTurnService {

    private final InterviewRepository interviewRepository;
    private final InterviewTurnRepository interviewTurnRepository;
    private final InterviewSecurity interviewSecurity;
    private final InterviewAiServiceResolver interviewAiServiceResolver;
    private final InterviewAiContextBuilder interviewAiContextBuilder;
    private final InterviewTurnMapper interviewTurnMapper;
    private final InterviewTurnFactory interviewTurnFactory;

    @Override
    public InterviewTurnResponse startFirstTurn(UUID interviewId) {

        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with id: " + interviewId));

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new InvalidStateException(
                    "Interview must be IN_PROGRESS to start a turn");
        }

        if (interviewTurnRepository.existsByInterviewId(interviewId)) {
            throw new InvalidStateException(
                    "Interview has already started");
        }

        InterviewTurn turn = interviewTurnFactory.createOpeningQuestion(interview);

        InterviewTurn savedTurn = interviewTurnRepository.save(turn);

        return interviewTurnMapper.toResponse(savedTurn);
    }


    @Override
    public InterviewTurnResponse submitAnswer(
            UUID interviewId,
            Long questionTurnId,
            SubmitAnswerRequest request) {

        Interview interview = getInterview(interviewId);

        validateInterview(interview);

        boolean closingMode = isClosingTime(interview);

        InterviewTurn questionTurn =
                getCurrentQuestionTurn(
                        interviewId,
                        questionTurnId);

        InterviewTurn savedAnswer =
                saveCandidateAnswer(
                        interview,
                        questionTurn,
                        request);

        List<InterviewTurn> previousTurns =
                getPreviousTurns(
                        interviewId,
                        savedAnswer);


        InterviewAiContext context =
                interviewAiContextBuilder.build(
                        interview,
                        previousTurns,
                        savedAnswer,
                        closingMode);

        InterviewAiService interviewAiService =
                interviewAiServiceResolver.resolve(
                        interview.getInterviewType());

        AiInterviewResponse aiResponse =
                interviewAiService.generateNextQuestion(context);

        InterviewTurn savedAiTurn =
                saveNextAiTurn(
                        interview,
                        savedAnswer,
                        aiResponse,
                        closingMode);

        if (closingMode) {

            interview.setStatus(InterviewStatus.COMPLETED);
            interview.setCompletedAt(LocalDateTime.now());

            interviewRepository.save(interview);
        }

        return interviewTurnMapper.toResponse(savedAiTurn);
    }

    @Override
    public List<InterviewTurnResponse> getInterviewTurns(
            UUID interviewId) {

        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found with id: " + interviewId));

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        return interviewTurnRepository
                .findByInterviewIdOrderByTurnNumberAsc(interviewId)
                .stream()
                .map(interviewTurnMapper::toResponse)
                .toList();
    }


    ///HELPER METHODS

    private Interview getInterview(UUID interviewId) {

        return interviewRepository.findById(interviewId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Interview not found with id: " + interviewId));
    }

    private void validateInterview(Interview interview) {

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
            throw new InvalidStateException(
                    "Interview must be IN_PROGRESS to submit an answer");
        }

        if (interview.getExpiresAt() != null
                && !LocalDateTime.now().isBefore(interview.getExpiresAt())) {

            throw new InvalidStateException(
                    "Interview has expired");
        }
    }

    private InterviewTurn getCurrentQuestionTurn(
            UUID interviewId, Long questionTurnId) {

        InterviewTurn questionTurn =
                interviewTurnRepository
                        .findByIdAndInterviewId(
                                questionTurnId, interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Question turn not found with id: " + questionTurnId));

        if (questionTurn.getSpeaker() != Speaker.AI
                || questionTurn.getTurnType() != TurnType.QUESTION) {
            throw new InvalidStateException(
                    "The specified turn is not an AI content");
        }

        InterviewTurn latestTurn =
                interviewTurnRepository
                        .findTopByInterviewIdOrderByTurnNumberDesc(interviewId)
                        .orElseThrow(() -> new InvalidStateException(
                                        "No interview turn exists"));

        if (!latestTurn.getId().equals(questionTurn.getId())) {
            throw new InvalidStateException(
                    "You can only answer the current interview content");
        }

        return questionTurn;
    }

    private InterviewTurn saveCandidateAnswer(
            Interview interview, InterviewTurn questionTurn,
            SubmitAnswerRequest request) {

        InterviewTurn answer =
                interviewTurnFactory.createCandidateAnswer(
                        interview, questionTurn, request);

        return interviewTurnRepository.save(answer);
    }


    private List<InterviewTurn> getPreviousTurns(
            UUID interviewId, InterviewTurn currentAnswer) {

        return interviewTurnRepository
                .findTop4ByInterviewIdOrderByTurnNumberDesc(interviewId)
                .reversed().stream()
                .filter(turn ->
                        !turn.getId().equals(currentAnswer.getId()))
                .toList();
    }

    private InterviewTurn saveNextAiTurn(
            Interview interview,
            InterviewTurn candidateAnswer, AiInterviewResponse aiResponse, boolean closingMode ) {

        InterviewTurn nextQuestion =
                interviewTurnFactory.createAiTurn(
                        interview, candidateAnswer, aiResponse,closingMode);

        return interviewTurnRepository.save(nextQuestion);
    }

    private boolean isClosingTime(Interview interview) {

        LocalDateTime closingThreshold =
                interview.getExpiresAt().minusSeconds(10);

        return !LocalDateTime.now().isBefore(closingThreshold);
    }

}