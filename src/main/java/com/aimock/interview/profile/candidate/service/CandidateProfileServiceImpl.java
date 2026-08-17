package com.aimock.interview.profile.candidate.service;

import com.aimock.interview.auth.security.SecurityUtils;
import com.aimock.interview.common.exception.DuplicateResourceException;
import com.aimock.interview.common.exception.ResourceNotFoundException;
import com.aimock.interview.profile.candidate.dto.CandidateProfileRequest;
import com.aimock.interview.profile.candidate.dto.CandidateProfileResponse;
import com.aimock.interview.profile.candidate.entity.CandidateProfile;
import com.aimock.interview.profile.candidate.repository.CandidateProfileRepository;
import com.aimock.interview.user.entity.User;
import com.aimock.interview.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    @Override
    public CandidateProfileResponse createProfile(
            CandidateProfileRequest request
    ) {

        User user = securityUtils.getCurrentUser();

        UUID userId = user.getId();

        if (candidateProfileRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException(
                    "Candidate profile already exists"
            );
        }

        CandidateProfile profile = new CandidateProfile();

        profile.setUser(user);
        profile.setCollege(request.getCollege());
        profile.setDegree(request.getDegree());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setPreferredDomain(request.getPreferredDomain());
        profile.setSkills(request.getSkills());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setTargetRole(request.getTargetRole());

        CandidateProfile savedProfile =
                candidateProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    @Override
    public CandidateProfileResponse getProfileById(UUID id) {

        CandidateProfile profile = candidateProfileRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student profile not found"
                        )
                );

        return mapToResponse(profile);
    }

    @Override
    public CandidateProfileResponse getMyProfile() {

        CandidateProfile profile = candidateProfileRepository
                .findByUserId(securityUtils.getCurrentUser().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate profile not found"
                        )
                );

        return mapToResponse(profile);
    }

    @Override
    public List<CandidateProfileResponse> getAllProfiles() {

        return candidateProfileRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CandidateProfileResponse updateMyProfile(
            CandidateProfileRequest request
    ) {

        UUID userId = securityUtils.getCurrentUser().getId();

        CandidateProfile profile = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate profile not found"
                        )
                );

        profile.setCollege(request.getCollege());
        profile.setDegree(request.getDegree());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setPreferredDomain(request.getPreferredDomain());
        profile.setSkills(request.getSkills());
        profile.setResumeUrl(request.getResumeUrl());
        profile.setTargetRole(request.getTargetRole());

        CandidateProfile updatedProfile =
                candidateProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    @Override
    public void deleteMyProfile() {

        UUID userId = securityUtils.getCurrentUser().getId();

        CandidateProfile profile = candidateProfileRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Candidate profile not found"
                        )
                );

        candidateProfileRepository.delete(profile);
    }

    private CandidateProfileResponse mapToResponse(
            CandidateProfile profile
    ) {

        return new CandidateProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getCollege(),
                profile.getDegree(),
                profile.getExperienceLevel(),
                profile.getPreferredDomain(),
                profile.getSkills(),
                profile.getResumeUrl(),
                profile.getTargetRole()
        );
    }
}