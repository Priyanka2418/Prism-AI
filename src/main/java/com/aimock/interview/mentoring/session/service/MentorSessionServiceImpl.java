package com.aimock.interview.mentoring.session.service;

import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.InvalidStateException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.mentoring.request.entity.MentorRequest;
import com.aimock.interview.mentoring.request.enums.RequestStatus;
import com.aimock.interview.mentoring.request.repository.MentorRequestRepository;
import com.aimock.interview.mentoring.session.common.SessionStateTransition;
import com.aimock.interview.mentoring.session.dto.MentorSessionResponse;
import com.aimock.interview.mentoring.session.entity.MentorSession;
import com.aimock.interview.mentoring.session.common.SessionStatus;
import com.aimock.interview.mentoring.session.common.MentorSessionMapper;
import com.aimock.interview.mentoring.session.repository.MentorSessionRepository;
import com.aimock.interview.mentoring.chat.repository.ChatMessageRepository;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.aimock.interview.profile.candidate.repository.CandidateProfileRepository;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.mentor.repository.MentorProfileRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorSessionServiceImpl implements MentorSessionService{

    private final MentorSessionRepository mentorSessionRepository;
    private final MentorRequestRepository mentorRequestRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SecurityUtils securityUtils;
    private final MentorSessionMapper mentorSessionMapper;
    private final CandidateProfileRepository candidateProfileRepository;
    private final MentorProfileRepository mentorProfileRepository;

    @Override
    @Transactional
    public MentorSession createSession(UUID mentorRequestId) {
        MentorRequest mentorRequest = mentorRequestRepository.findById(mentorRequestId)
                .orElseThrow(()->new ResourceNotFoundException("Mentoring Request not found"));

        if (mentorRequest.getStatus() != RequestStatus.ACCEPTED) {
            throw new InvalidStateException(
                    "A session can only be created for an accepted mentoring request");
        }

        LocalDateTime scheduledStartAt =
                mentorRequest.getRequestedStartTime();

        LocalDateTime scheduledEndAt =
                scheduledStartAt.plusMinutes(
                        mentorRequest.getRequestedDurationMinutes());

        MentorSession mentorSession = new MentorSession();

        mentorSession.setMentorRequest(mentorRequest);
        mentorSession.setScheduledStartAt(scheduledStartAt);
        mentorSession.setScheduledEndAt(scheduledEndAt);
        mentorSession.setStatus(SessionStatus.UPCOMING);

        return mentorSessionRepository.save(mentorSession);
    }

    @Override
    public List<MentorSessionResponse> getCandidateSessions() {

        User currentUser = securityUtils.getCurrentUser();

        CandidateProfile candidateProfile =
                candidateProfileRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Candidate profile not found"));

        return mentorSessionRepository
                .findByMentorRequestStudentIdAndDeletedByStudentFalse(candidateProfile.getId())
                .stream()
                .map(session ->
                        mentorSessionMapper.toResponse(
                                session,
                                session.getMentorRequest()
                                        .getMentor()
                                        .getDisplayName()
                        ))
                .toList();
    }

    @Override
    public List<MentorSessionResponse> getMentorSessions() {

        User currentUser = securityUtils.getCurrentUser();

        MentorProfile mentorProfile =
                mentorProfileRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor profile not found"));

        return mentorSessionRepository
                .findByMentorRequestMentorIdAndDeletedByMentorFalse(mentorProfile.getId())
                .stream()
                .map(session ->
                        mentorSessionMapper.toResponse(
                                session,
                                session.getMentorRequest()
                                        .getStudent()
                                        .getDisplayName()
                        ))
                .toList();
    }

    @Override
    public MentorSessionResponse getSession(UUID sessionId) {

        MentorSession mentorSession =
                mentorSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor session not found"));

        User currentUser = securityUtils.getCurrentUser();

        MentorRequest mentorRequest =
                mentorSession.getMentorRequest();

        String otherParticipantName;

        // Current user is the candidate
        if (mentorRequest.getStudent()
                .getUser()
                .getId()
                .equals(currentUser.getId())) {

            otherParticipantName =
                    mentorRequest.getMentor().getDisplayName();

        }

        // Current user is the mentor
        else if (mentorRequest.getMentor()
                .getUser()
                .getId()
                .equals(currentUser.getId())) {

            otherParticipantName =
                    mentorRequest.getStudent().getDisplayName();

        } else {
            throw new ForbiddenException("You are not a participant of this mentoring session");
        }

        return mentorSessionMapper.toResponse(
                mentorSession,
                otherParticipantName);
    }

    @Override
    @Transactional
    public void deleteSession(UUID sessionId) {

        MentorSession mentorSession =
                mentorSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor session not found"));

        User currentUser = securityUtils.getCurrentUser();

        MentorRequest mentorRequest = mentorSession.getMentorRequest();

        UUID studentUserId = mentorRequest.getStudent().getUser().getId();
        UUID mentorUserId = mentorRequest.getMentor().getUser().getId();
        UUID currentUserId = currentUser.getId();

        if (currentUserId.equals(studentUserId)) {
            mentorSession.setDeletedByStudent(true);
        } else if (currentUserId.equals(mentorUserId)) {
            mentorSession.setDeletedByMentor(true);
        } else if (currentUser.getRole() == com.aimock.interview.common.enums.Role.ADMIN) {
            mentorSession.setDeletedByStudent(true);
            mentorSession.setDeletedByMentor(true);
        } else {
            throw new ForbiddenException("You are not authorized to delete this mentoring session");
        }

        // Only physically remove records if both sides have deleted the session
        if (mentorSession.isDeletedByStudent() && mentorSession.isDeletedByMentor()) {
            chatMessageRepository.deleteByMentorSessionId(sessionId);
            mentorSessionRepository.delete(mentorSession);
        } else {
            mentorSessionRepository.save(mentorSession);
        }
    }

}
