package com.aimock.interview.mentoring.request.service;

import com.aimock.interview.mentoring.request.dto.CreateMentorRequest;
import com.aimock.interview.mentoring.request.dto.MentorRequestResponse;
import com.aimock.interview.mentoring.request.dto.RejectMentorRequest;

import java.util.List;
import java.util.UUID;

public interface MentorRequestService {

    MentorRequestResponse createRequest(
            UUID mentorId, CreateMentorRequest request);

    List<MentorRequestResponse> getCandidateRequests();

    List<MentorRequestResponse> getMentorPendingRequests();

    MentorRequestResponse acceptRequest(UUID requestId);

    MentorRequestResponse rejectRequest(
            UUID requestId, RejectMentorRequest request);
}