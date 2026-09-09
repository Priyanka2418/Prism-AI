package com.aimock.interview.mentoring.session.repository;

import com.aimock.interview.mentoring.session.entity.MentorSession;
import com.aimock.interview.mentoring.session.common.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
    SELECT ms
    FROM MentorSession ms
    JOIN FETCH ms.mentorRequest mr
    JOIN FETCH mr.student student
    JOIN FETCH student.user studentUser
    JOIN FETCH mr.mentor mentor
    JOIN FETCH mentor.user mentorUser
    WHERE ms.id = :sessionId
""")
    Optional<MentorSession> findByIdForChatAuthorization(
            @Param("sessionId") UUID sessionId
    );

}