package com.aimock.interview.interview.commons;

import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("interviewSecurity")
@RequiredArgsConstructor
public class InterviewSecurity {

    public boolean isOwner(Interview interview) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        return interview.getStudent()
                .getUser()
                .getId()
                .equals(user.getId());
    }
}