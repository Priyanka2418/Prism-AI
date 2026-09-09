package com.aimock.interview.interview.commons;

import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.mentoring.request.enums.RequestStatus;
import com.aimock.interview.mentoring.request.repository.MentorRequestRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("interviewSecurity")
@RequiredArgsConstructor
public class InterviewSecurity {

    private final MentorRequestRepository mentorRequestRepository;

    public boolean isOwner(Interview interview) {
        User user = getCurrentUser();
        return interview.getStudent()
                .getUser()
                .getId()
                .equals(user.getId());
    }

    /**
     * Returns true if the current user is:
     * 1. The student who owns the interview, OR
     * 2. A MENTOR who has an ACCEPTED MentorRequest that references this interview
     */
    public boolean canAccess(Interview interview) {
        if (isOwner(interview)) return true;

        User user = getCurrentUser();
        // Check if user has MENTOR role
        boolean isMentor = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MENTOR"));
        if (!isMentor) return false;

        // Check if there's an accepted mentor request for this interview linked to this mentor's user account
        return !mentorRequestRepository
                .findByMentorUserIdAndInterviewId(user.getId(), interview.getId())
                .stream()
                .filter(r -> r.getStatus() == RequestStatus.ACCEPTED)
                .toList()
                .isEmpty();
    }

    private User getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}