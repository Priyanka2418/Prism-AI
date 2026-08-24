package com.aimock.interview.mentoring.request.repository;


import com.aimock.interview.mentoring.request.entity.MentorRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MentorRequestRepository extends JpaRepository<MentorRequest, UUID> {

}
