package com.aimock.interview.mentoring.request.repository;

import com.aimock.interview.mentoring.request.entity.MentorRequest;
import com.aimock.interview.mentoring.request.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface MentorRequestRepository extends JpaRepository<MentorRequest, UUID> {

    List<MentorRequest> findByMentorIdAndStatus(UUID mentorId, RequestStatus status);

    List<MentorRequest> findByStudentId(UUID studentId);

    List<MentorRequest> findByInterviewId(UUID interviewId);

    @Query("SELECT r FROM MentorRequest r WHERE r.mentor.user.id = :userId AND r.interview.id = :interviewId")
    List<MentorRequest> findByMentorUserIdAndInterviewId(@Param("userId") UUID userId, @Param("interviewId") UUID interviewId);
}
