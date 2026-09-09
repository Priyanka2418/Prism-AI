package com.aimock.interview.interview.lifecycle.service;

import com.aimock.interview.common.exception.ForbiddenException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.interview.ai.evaluation.repository.InterviewFeedbackRepository;
import com.aimock.interview.interview.commons.InterviewSecurity;
import com.aimock.interview.interview.commons.enums.InterviewStatus;
import com.aimock.interview.interview.lifecycle.dto.CreateInterviewRequest;
import com.aimock.interview.interview.lifecycle.dto.InterviewResponse;
import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.lifecycle.repository.InterviewRepository;
import com.aimock.interview.interview.turn.repository.InterviewTurnRepository;
import com.aimock.interview.mentoring.request.entity.MentorRequest;
import com.aimock.interview.mentoring.request.repository.MentorRequestRepository;
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
    private final InterviewTurnRepository interviewTurnRepository;
    private final InterviewFeedbackRepository feedbackRepository;
    private final MentorRequestRepository mentorRequestRepository;
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

        CandidateProfile candidateProfile = getOrCreateCandidateProfile(user, request);

        Interview interview = new Interview();

        interview.setTitle(request.title() != null && !request.title().isBlank()
                ? request.title().trim()
                : request.targetRole() + " (" + request.interviewType() + ")");
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

        if (!interviewSecurity.canAccess(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        return mapToResponse(interview);
    }

    @Override
    public List<InterviewResponse> getMyInterviews() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        CandidateProfile candidateProfile =
                candidateProfileRepository.findByUserId(user.getId()).orElse(null);

        if (candidateProfile == null) {
            return List.of();
        }

        return interviewRepository
                .findByStudentIdOrderByCreatedAtDesc(candidateProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteInterview(UUID interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException("You are not authorized to delete this interview");
        }

        // Unlink any mentoring requests referencing this interview
        List<MentorRequest> relatedRequests = mentorRequestRepository.findByInterviewId(interviewId);
        for (MentorRequest req : relatedRequests) {
            req.setInterview(null);
        }
        if (!relatedRequests.isEmpty()) {
            mentorRequestRepository.saveAll(relatedRequests);
        }

        // Delete associated feedback and turns
        feedbackRepository.deleteByInterviewId(interviewId);
        interviewTurnRepository.deleteByInterviewId(interviewId);

        // Delete interview entity
        interviewRepository.delete(interview);
    }

    @Override
    public InterviewResponse completeInterview(UUID interviewId) {
        Interview interview =
                interviewRepository.findById(interviewId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Interview not found with id: " + interviewId));

        if (!interviewSecurity.isOwner(interview)) {
            throw new ForbiddenException(
                    "You are not allowed to access this interview");
        }

        if (interview.getStatus() == InterviewStatus.IN_PROGRESS) {
            stateTransition.transition(interview, InterviewStatus.COMPLETED);
            Interview savedInterview = interviewRepository.save(interview);
            return mapToResponse(savedInterview);
        }

        return mapToResponse(interview);
    }

    private CandidateProfile getOrCreateCandidateProfile(User user, CreateInterviewRequest request) {
        return candidateProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    CandidateProfile profile = new CandidateProfile();
                    profile.setUser(user);
                    String email = user.getEmail();
                    String name = email.contains("@") ? email.split("@")[0] : "Candidate";
                    profile.setDisplayName(name.substring(0, 1).toUpperCase() + (name.length() > 1 ? name.substring(1) : ""));
                    if (request != null) {
                        try {
                            profile.setTargetRole(com.aimock.interview.common.enums.TargetRole.valueOf(
                                     request.targetRole().trim().toUpperCase().replace(" ", "_").replace("-", "_")));
                        } catch (Exception ignored) {
                            profile.setTargetRole(com.aimock.interview.common.enums.TargetRole.SOFTWARE_ENGINEER);
                        }
                        profile.setExperienceLevel(request.experienceLevel());
                    }
                    return candidateProfileRepository.save(profile);
                });
    }

    private InterviewResponse mapToResponse(
            Interview interview) {

        return new InterviewResponse(
                interview.getId(),
                interview.getTitle() != null ? interview.getTitle() : interview.getTargetRole(),
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