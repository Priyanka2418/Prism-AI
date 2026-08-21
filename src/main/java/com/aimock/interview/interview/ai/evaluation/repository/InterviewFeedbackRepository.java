package com.aimock.interview.interview.ai.evaluation.repository;

import com.aimock.interview.interview.ai.evaluation.entity.InterviewFeedback;
import com.aimock.interview.interview.ai.evaluation.enums.FeedbackStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InterviewFeedbackRepository
        extends JpaRepository<InterviewFeedback, UUID> {

    Optional<InterviewFeedback> findByInterviewId(
            UUID interviewId);

    boolean existsByInterviewId(
            UUID interviewId);

    boolean existsByInterviewIdAndStatus(
            UUID interviewId,
            FeedbackStatus status);
}