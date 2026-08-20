package com.aimock.interview.interview;

import com.aimock.interview.interview.repository.InterviewRepository;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("interviewSecurity")
@RequiredArgsConstructor
public class InterviewSecurity {

    private final InterviewRepository interviewRepository;

    public boolean isOwner(UUID interviewId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        return interviewRepository.findById(interviewId)
                .map(interview ->
                        interview.getStudent()
                                .getUser()
                                .getId()
                                .equals(user.getId())
                )
                .orElse(false);
    }
}