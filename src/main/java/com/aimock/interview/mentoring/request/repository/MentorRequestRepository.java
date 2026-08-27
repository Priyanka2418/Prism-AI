package com.aimock.interview.mentoring.request.repository;


import com.aimock.interview.mentoring.request.entity.MentorRequest;
import com.aimock.interview.mentoring.request.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MentorRequestRepository extends JpaRepository<MentorRequest, UUID> {

    List<MentorRequest> findByMentorIdAndStatus(UUID mentorId, RequestStatus status);

    List<MentorRequest> findByStudentId(UUID studentId);
}
