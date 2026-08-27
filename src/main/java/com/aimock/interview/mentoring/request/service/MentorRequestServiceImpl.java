package com.aimock.interview.mentoring.request.service;

import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.InvalidStateException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.lifecycle.repository.InterviewRepository;
import com.aimock.interview.mentoring.request.dto.CreateMentorRequest;
import com.aimock.interview.mentoring.request.dto.MentorRequestResponse;
import com.aimock.interview.mentoring.request.dto.RejectMentorRequest;
import com.aimock.interview.mentoring.request.entity.MentorRequest;
import com.aimock.interview.mentoring.request.enums.RequestStatus;
import com.aimock.interview.mentoring.request.mapper.MentorRequestMapper;
import com.aimock.interview.mentoring.request.repository.MentorRequestRepository;
import com.aimock.interview.mentoring.session.service.MentorSessionService;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.aimock.interview.profile.candidate.repository.CandidateProfileRepository;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.mentor.enums.PublicProfileStatus;
import com.aimock.interview.profile.mentor.repository.MentorProfileRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aimock.interview.common.enums.VerificationStatus;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorRequestServiceImpl implements MentorRequestService {

    private final MentorRequestRepository mentorRequestRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final InterviewRepository interviewRepository;
    private final MentorRequestMapper mentorRequestMapper;
    private final SecurityUtils securityUtils;
    private final MentorSessionService mentorSessionService;

    @Override
    public MentorRequestResponse createRequest(
            UUID mentorId, CreateMentorRequest request) {

        User currentUser = securityUtils.getCurrentUser();

        CandidateProfile candidateProfile =
                candidateProfileRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Candidate profile not found"));

        MentorProfile mentorProfile =
                mentorProfileRepository.findById(mentorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Mentor profile not found"));

        validateMentor(mentorProfile);

        Interview interview =
                interviewRepository.findById(request.interviewId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Interview not found"));

        validateInterviewOwnership(interview, candidateProfile);

        MentorRequest mentorRequest =
                mentorRequestMapper.toEntity(request);

        mentorRequest.setStudent(candidateProfile);
        mentorRequest.setMentor(mentorProfile);
        mentorRequest.setInterview(interview);
        mentorRequest.setStatus(RequestStatus.PENDING);

        MentorRequest savedRequest =
                mentorRequestRepository.save(mentorRequest);

        return mentorRequestMapper.toResponse(savedRequest);
    }
    @Override
    public List<MentorRequestResponse> getCandidateRequests() {

        User currentUser = securityUtils.getCurrentUser();

        CandidateProfile candidateProfile =
                candidateProfileRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Candidate profile not found"));

        return mentorRequestRepository
                .findByStudentId(candidateProfile.getId())
                .stream()
                .map(mentorRequestMapper::toResponse)
                .toList();
    }

    @Override
    public List<MentorRequestResponse> getMentorPendingRequests() {

        User currentUser = securityUtils.getCurrentUser();

        MentorProfile mentorProfile =
                mentorProfileRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentor profile not found"));

        List<MentorRequest> requests =
                mentorRequestRepository.findByMentorIdAndStatus(
                        mentorProfile.getId(),
                        RequestStatus.PENDING);

        return requests.stream()
                .map(mentorRequestMapper::toResponse)
                .toList();
    }
    @Override
    @Transactional
    public MentorRequestResponse acceptRequest(UUID requestId) {

        User currentUser = securityUtils.getCurrentUser();

        MentorProfile mentorProfile =
                mentorProfileRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentor profile not found"));

        MentorRequest mentorRequest =
                mentorRequestRepository.findById(requestId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentoring request not found"));

        if (!mentorRequest.getMentor().getId().equals(mentorProfile.getId())) {
            throw new ForbiddenException(
                    "You are not authorized to manage this mentoring request");
        }

        if (mentorRequest.getStatus() != RequestStatus.PENDING) {
            throw new InvalidStateException(
                    "Only pending requests can be accepted");
        }

        mentorRequest.setStatus(RequestStatus.ACCEPTED);
        mentorRequest.setMentorAcceptedAt(LocalDateTime.now());

        mentorSessionService.createSession(mentorRequest.getId());

        return mentorRequestMapper.toResponse(mentorRequest);
    }

    @Override
    public MentorRequestResponse rejectRequest(
            UUID requestId, RejectMentorRequest request) {

        User currentUser = securityUtils.getCurrentUser();

        MentorProfile mentorProfile =
                mentorProfileRepository.findByUserId(currentUser.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentor profile not found"));

        MentorRequest mentorRequest =
                mentorRequestRepository.findById(requestId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mentoring request not found"));

        if (!mentorRequest.getMentor().getId().equals(mentorProfile.getId())) {
            throw new ForbiddenException(
                    "You are not authorized to manage this mentoring request");
        }

        if (mentorRequest.getStatus() != RequestStatus.PENDING) {
            throw new InvalidStateException(
                    "Only pending requests can be rejected");
        }

        mentorRequest.setStatus(RequestStatus.REJECTED);
        mentorRequest.setMentorRejectionReason(
                request.rejectionReason());

        MentorRequest savedRequest =
                mentorRequestRepository.save(mentorRequest);

        return mentorRequestMapper.toResponse(savedRequest);
    }

    private void validateMentor(MentorProfile mentorProfile) {

        if (mentorProfile.getVerificationStatus()
                != VerificationStatus.VERIFIED) {

            throw new IllegalStateException(
                    "Mentor is not verified");
        }

        if (mentorProfile.getPublicProfileStatus()
                != PublicProfileStatus.COMPLETED) {

            throw new IllegalStateException(
                    "Mentor profile is not completed");
        }
    }

    private void validateInterviewOwnership(
            Interview interview,
            CandidateProfile candidateProfile) {

        if (!interview.getStudent()
                .getId()
                .equals(candidateProfile.getId())) {

            throw new ForbiddenException(
                    "Interview does not belong to the candidate");
        }
    }

}