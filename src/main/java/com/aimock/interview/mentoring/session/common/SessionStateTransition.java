package com.aimock.interview.mentoring.session.common;

import java.util.Map;
import java.util.Set;

public final class SessionStateTransition {

    private static final Map<SessionStatus, Set<SessionStatus>> ALLOWED_TRANSITIONS =
            Map.of(SessionStatus.UPCOMING, Set.of(SessionStatus.IN_PROGRESS),
                    SessionStatus.IN_PROGRESS, Set.of(SessionStatus.COMPLETED),
                    SessionStatus.COMPLETED, Set.of());

    private SessionStateTransition() {
    }

    public static boolean isAllowed(
            SessionStatus currentStatus, SessionStatus targetStatus) {
        return ALLOWED_TRANSITIONS
                .getOrDefault(currentStatus, Set.of()).contains(targetStatus);
    }
}