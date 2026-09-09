package com.aimock.interview.mentoring.session.common;

import com.aimock.interview.mentoring.session.dto.MentorSessionResponse;
import com.aimock.interview.mentoring.session.entity.MentorSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MentorSessionMapper {

    @Mapping(
            target = "interviewId",
            expression = "java(mentorSession.getMentorRequest().getInterview() != null ? mentorSession.getMentorRequest().getInterview().getId() : null)"
    )
    @Mapping(
            target = "otherParticipantName",
            expression = "java(otherParticipantName)"
    )
    MentorSessionResponse toResponse(
            MentorSession mentorSession,
            String otherParticipantName
    );
}