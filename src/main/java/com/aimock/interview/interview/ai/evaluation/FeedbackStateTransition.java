package com.aimock.interview.interview.ai.evaluation;

import com.aimock.interview.interview.ai.evaluation.entity.InterviewFeedback;
import com.aimock.interview.interview.ai.evaluation.enums.FeedbackStatus;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class FeedbackStateTransition {

    private final Map<FeedbackStatus, Set<FeedbackStatus>> transitions =
            new EnumMap<>(FeedbackStatus.class);

    public FeedbackStateTransition() {

        transitions.put(
                FeedbackStatus.PENDING,
                EnumSet.of(FeedbackStatus.GENERATING)
        );

        transitions.put(
                FeedbackStatus.GENERATING,
                EnumSet.of(
                        FeedbackStatus.COMPLETED,
                        FeedbackStatus.FAILED
                )
        );

        transitions.put(
                FeedbackStatus.FAILED,
                EnumSet.of(FeedbackStatus.GENERATING)
        );

        transitions.put(
                FeedbackStatus.COMPLETED,
                EnumSet.noneOf(FeedbackStatus.class)
        );
    }

    public void moveTo(
            InterviewFeedback feedback,
            FeedbackStatus targetStatus) {

        FeedbackStatus currentStatus =
                feedback.getStatus();

        Set<FeedbackStatus> allowedTransitions =
                transitions.getOrDefault(
                        currentStatus,
                        EnumSet.noneOf(FeedbackStatus.class)
                );

        if (!allowedTransitions.contains(targetStatus)) {

            throw new IllegalStateException(
                    "Invalid feedback state transition: "
                            + currentStatus
                            + " -> "
                            + targetStatus
            );
        }

        feedback.setStatus(targetStatus);
    }
}