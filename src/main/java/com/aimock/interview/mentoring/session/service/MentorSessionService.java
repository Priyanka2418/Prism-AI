package com.aimock.interview.mentoring.session.service;

import com.aimock.interview.mentoring.session.dto.MentorSessionResponse;
import com.aimock.interview.mentoring.session.entity.MentorSession;

import java.util.List;
import java.util.UUID;

public interface MentorSessionService {

    MentorSession createSession(UUID mentorRequestId);

    List<MentorSessionResponse> getCandidateSessions();

    List<MentorSessionResponse> getMentorSessions();

    MentorSessionResponse getSession(UUID sessionId);

}