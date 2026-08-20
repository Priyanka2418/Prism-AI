package com.aimock.interview.interview.lifecycle.service;

import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.interview.commons.InterviewSecurity;
import com.aimock.interview.interview.commons.enums.InterviewStatus;
import com.aimock.interview.interview.lifecycle.dto.CreateInterviewRequest;
import com.aimock.interview.interview.lifecycle.dto.InterviewResponse;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.lifecycle.repository.InterviewRepository;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.aimock.interview.profile.candidate.repository.CandidateProfileRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewStateTransition stateTransition;
    private final CandidateProfileRepository candidateProfileRepository;
    private final InterviewSecurity interviewSecurity;


    @Override
    public InterviewResponse createInterview(
            CreateInterviewRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        User user = (User) authentication.getPrincipal();

        CandidateProfile candidateProfile =
                candidateProfileRepository.findByUserId(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Candidate profile not found"));

        Interview interview = new Interview();

        interview.setStudent(candidateProfile);
        interview.setInterviewType(request.interviewType());
        interview.setInterviewDifficulty(
                request.interviewDifficulty());


        interview.setTargetRole(request.targetRole());
        interview.setExperienceLevel(request.experienceLevel());
        interview.setTopics(request.topics());
        interview.setDurationMinutes(request.durationMinutes());
        interview.setStatus(InterviewStatus.CREATED);

        Interview savedInterview =
                interviewRepository.save(interview);

        return mapToResponse(savedInterview);
    }


    @Override
    public InterviewResponse startInterview(UUID interviewId) {

        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Interview not found"));

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        stateTransition.transition(
                interview, InterviewStatus.IN_PROGRESS);

        interview.setExpiresAt(
                interview.getStartedAt().plusMinutes(interview.getDurationMinutes()));

        Interview savedInterview =
                interviewRepository.save(interview);

        return mapToResponse(savedInterview);
    }


    @Override
    public InterviewResponse cancelInterview(UUID interviewId) {

        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Interview not found"));

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        stateTransition.transition(
                interview, InterviewStatus.CANCELLED);

        Interview savedInterview =
                interviewRepository.save(interview);

        return mapToResponse(savedInterview);
    }


    @Override
    public void completeExpiredInterviews() {

        LocalDateTime now = LocalDateTime.now();

        List<Interview> expiredInterviews =
                interviewRepository
                        .findByStatusAndExpiresAtLessThanEqual(
                                InterviewStatus.IN_PROGRESS, now);

        for (Interview interview : expiredInterviews) {

            stateTransition.transition(
                    interview,
                    InterviewStatus.COMPLETED);
        }

        interviewRepository.saveAll(expiredInterviews);
    }


    @Override
    public InterviewResponse getInterview(UUID interviewId) {

        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Interview not found"));

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        return mapToResponse(interview);
    }

    private InterviewResponse mapToResponse(
            Interview interview) {

        return new InterviewResponse(
                interview.getId(),
                interview.getInterviewType(),
                interview.getInterviewDifficulty(),
                interview.getTargetRole(),
                interview.getExperienceLevel(),
                interview.getTopics(),
                interview.getDurationMinutes(),
                interview.getStatus(),
                interview.getStartedAt(),
                interview.getExpiresAt(),
                interview.getCompletedAt(),
                interview.getCreatedAt(),
                interview.getUpdatedAt());
    }
}