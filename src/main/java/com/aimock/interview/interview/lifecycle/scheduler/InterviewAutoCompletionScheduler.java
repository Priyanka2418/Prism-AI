package com.aimock.interview.interview.lifecycle.scheduler;

import com.aimock.interview.interview.lifecycle.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InterviewAutoCompletionScheduler {

    private final InterviewService interviewService;

    @Scheduled(fixedDelay = 10000)
    public void completeExpiredInterviews() {
        interviewService.completeExpiredInterviews();
    }
}