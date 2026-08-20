package com.aimock.interview.interview.turn.repository;

import com.aimock.interview.interview.commons.enums.TurnType;
import com.aimock.interview.interview.turn.entity.InterviewTurn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InterviewTurnRepository extends JpaRepository<InterviewTurn, Long> {

    List<InterviewTurn> findByInterviewIdOrderByTurnNumberAsc(UUID interviewId);

    Optional<InterviewTurn> findByIdAndInterviewId(
            Long turnId,
            UUID interviewId);

    Optional<InterviewTurn> findTopByInterviewIdOrderByTurnNumberDesc(
            UUID interviewId);

    List<InterviewTurn> findTop4ByInterviewIdOrderByTurnNumberDesc(
            UUID interviewId);

    boolean existsByInterviewId(UUID interviewId);


    List<InterviewTurn> findByInterviewIdAndTurnTypeOrderByTurnNumberAsc(
            UUID interviewId,
            TurnType turnType);

}