package com.aimock.interview.profile.mentor.repository;

import com.aimock.interview.common.enums.VerificationStatus;
import com.aimock.interview.profile.mentor.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, UUID> {

    Optional<MentorProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    List<MentorProfile> findByVerificationStatus(
            VerificationStatus verificationStatus
    );
}

