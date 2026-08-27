package com.aimock.interview.mentoring.session.repository;

import com.aimock.interview.mentoring.session.entity.MentorSession;
import com.aimock.interview.mentoring.session.common.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorSessionRepository
        extends JpaRepository<MentorSession, UUID> {

    Optional<MentorSession> findByMentorRequestId(UUID mentorRequestId);

    //candidate session UIs
    List<MentorSession> findByMentorRequestStudentId(UUID studentId);

    //Mentor session UIs
    List<MentorSession> findByMentorRequestMentorId(UUID mentorId);


    List<MentorSession> findByStatusAndScheduledStartAtLessThanEqual(
                SessionStatus status, LocalDateTime now);

    List<MentorSession> findByStatusAndScheduledEndAtLessThanEqual(
            SessionStatus status, LocalDateTime now);

}