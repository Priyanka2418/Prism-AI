package com.aimock.interview.interview.repository;

import com.aimock.interview.interview.entity.Interview;
import com.aimock.interview.interview.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    List<Interview> findByStatusAndExpiresAtLessThanEqual(
            InterviewStatus status,
            LocalDateTime time);

}