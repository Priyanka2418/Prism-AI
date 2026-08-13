package com.aimock.interview.profile.candidate.repository;

import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID> {

    Optional<CandidateProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}