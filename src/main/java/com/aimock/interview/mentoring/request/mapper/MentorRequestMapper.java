package com.aimock.interview.mentoring.request.mapper;

import com.aimock.interview.mentoring.request.dto.CreateMentorRequest;
import com.aimock.interview.mentoring.request.dto.MentorRequestResponse;
import com.aimock.interview.mentoring.request.entity.MentorRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MentorRequestMapper {

    @Mapping(target = "mentorName", source = "mentor.displayName")
    @Mapping(target = "interviewId", source = "interview.id")
    MentorRequestResponse toResponse(MentorRequest request);

    MentorRequest toEntity(CreateMentorRequest request);
}