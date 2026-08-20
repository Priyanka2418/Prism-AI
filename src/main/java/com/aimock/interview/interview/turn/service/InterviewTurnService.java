package com.aimock.interview.interview.turn.service;

import com.aimock.interview.interview.turn.dto.InterviewTurnResponse;
import com.aimock.interview.interview.turn.dto.SubmitAnswerRequest;

import java.util.List;
import java.util.UUID;

public interface InterviewTurnService {

    InterviewTurnResponse startFirstTurn(UUID interviewId);

    InterviewTurnResponse submitAnswer(
            UUID interviewId,
            Long questionTurnId,
            SubmitAnswerRequest request
    );

    List<InterviewTurnResponse> getInterviewTurns(UUID interviewId);
}
