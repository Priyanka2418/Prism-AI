package com.aimock.interview.mentoring.request.service;

import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.lifecycle.repository.InterviewRepository;
import com.aimock.interview.mentoring.request.dto.CreateMentorRequest;
import com.aimock.interview.mentoring.request.dto.MentorRequestResponse;
import com.aimock.interview.mentoring.request.entity.MentorRequest;
import com.aimock.interview.mentoring.request.enums.RequestStatus;
import com.aimock.interview.mentoring.request.mapper.MentorRequestMapper;
import com.aimock.interview.mentoring.request.repository.MentorRequestRepository;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.aimock.interview.profile.candidate.repository.CandidateProfileRepository;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import com.aimock.interview.profile.mentor.enums.PublicProfileStatus;
import com.aimock.interview.profile.mentor.repository.MentorProfileRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.aimock.interview.common.enums.VerificationStatus;
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