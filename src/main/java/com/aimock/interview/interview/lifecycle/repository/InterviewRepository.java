package com.aimock.interview.interview.lifecycle.repository;

import com.aimock.interview.interview.lifecycle.entity.Interview;
import com.aimock.interview.interview.commons.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface InterviewRepository extends JpaRepository<Interview, UUID> {

    List<Interview> findByStatusAndExpiresAtLessThanEqual(
            InterviewStatus status,
            LocalDateTime time);

}