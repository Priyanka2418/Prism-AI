package com.aimock.interview.interview.turn.mapper;

import com.aimock.interview.interview.turn.dto.InterviewTurnResponse;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewTurnMapper {

    @Mapping(target = "interviewId", source = "interview.id")
    @Mapping(target = "parentTurnId", source = "parentTurn.id")
    InterviewTurnResponse toResponse(InterviewTurn turn);
}