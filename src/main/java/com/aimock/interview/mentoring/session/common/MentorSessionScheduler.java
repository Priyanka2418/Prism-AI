package com.aimock.interview.mentoring.session.common;

import com.aimock.interview.mentoring.session.repository.MentorSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorSessionScheduler {

    private final MentorSessionRepository mentorSessionRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void updateSessionStatuses() {

        LocalDateTime now = LocalDateTime.now();

        mentorSessionRepository
                .findByStatusAndScheduledStartAtLessThanEqual(
                        SessionStatus.UPCOMING, now)
                .forEach(session ->
                        session.transitionTo(SessionStatus.IN_PROGRESS));

        mentorSessionRepository
                .findByStatusAndScheduledEndAtLessThanEqual(
                        SessionStatus.IN_PROGRESS, now)
                .forEach(session ->
                        session.transitionTo(SessionStatus.COMPLETED));
    }
}