package com.aimock.interview.mentoring.request.service;

import com.aimock.interview.mentoring.request.dto.CreateMentorRequest;
import com.aimock.interview.mentoring.request.dto.MentorRequestResponse;

import java.util.UUID;

public interface MentorRequestService {

    MentorRequestResponse createRequest(
            UUID mentorId, CreateMentorRequest request);


}