package com.aimock.interview.interview.lifecycle.service;

import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.commons.enums.InterviewStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Component
public class InterviewStateTransition {

    private static final Map<InterviewStatus, Set<InterviewStatus>> TRANSITIONS =
            Map.of(
                    InterviewStatus.CREATED,
                    Set.of(InterviewStatus.IN_PROGRESS),

                    InterviewStatus.IN_PROGRESS,
                    Set.of(
                            InterviewStatus.COMPLETED,
                            InterviewStatus.CANCELLED
                    ),

                    InterviewStatus.COMPLETED,
                    Set.of(),

                    InterviewStatus.CANCELLED,
                    Set.of()
            );

    public void transition(
            Interview interview,
            InterviewStatus nextStatus
    ) {

        InterviewStatus currentStatus = interview.getStatus();

        boolean validTransition = TRANSITIONS
                .getOrDefault(currentStatus, Set.of())
                .contains(nextStatus);

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid interview state transition: "
                            + currentStatus
                            + " -> "
                            + nextStatus
            );
        }

        interview.setStatus(nextStatus);

        if (nextStatus == InterviewStatus.IN_PROGRESS) {
            interview.setStartedAt(LocalDateTime.now());
        }

        if (nextStatus == InterviewStatus.COMPLETED) {
            interview.setCompletedAt(LocalDateTime.now());
        }
    }
}